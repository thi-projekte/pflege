package de.pflegital.chatbot;
 
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
 
import org.slf4j.Logger;
 
import de.pflegital.chatbot.model.CareType;
import de.pflegital.chatbot.model.Caregiver;
import de.pflegital.chatbot.model.Period;
import de.pflegital.chatbot.model.Reason;
 
@ApplicationScoped
public class Pflegebot {
    @Inject
    SessionStore sessionStore;
 
    @Inject
    AiService aiService;
 
    @Inject
    FormDataPresenter formDataPresenter;
 
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
 
    private final Logger LOG = org.slf4j.LoggerFactory.getLogger(Pflegebot.class);
 
    public ChatResponse processUserInput(String waId, String userInput) {
        LOG.info("IM PFLEGEBOT: {}", waId);
        if (sessionStore.getFormData(waId) == null) {
            FormData aiResponse = new FormData();
            sessionStore.setFormData(waId, aiResponse);
            LOG.info("New empty form data created");
        }
        FormData session = sessionStore.getFormData(waId);
        LOG.info(" sessionId = waId: {}", session);
        if (session == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }
 
        LOG.info("User writes: {}", userInput);
 
        String jsonFormData = formDataPresenter.present(session);
        String prompt = """
                CONTEXT BEGIN
                %s
                CONTEXT END
 
                PREVIOUS QUESTION BY AI:
                %s
 
                ANSWER BY USER:
                %s
                """.formatted(jsonFormData, session.getChatbotMessage(), userInput);
 
        LOG.info("Prompt to AI: {}", prompt);
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        FormData updatedResponse = aiService.chatWithAiStructured(waId, prompt, currentDate);
 
        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        }
 
        if (!updatedResponse.isComplete()) {
            // Neues vollständiges FormData-Objekt mit Dummy-Daten
            FormData formData = new FormData();
            formData.setCareType(CareType.STUNDENWEISE);
            formData.setCareLevel(3);
            formData.setCareDurationMin6Months(true);
            formData.setLegalAcknowledgement(true);
            formData.setHomeCare(true);
            formData.setReason(Reason.URLAUB);
            formData.setConversationPartner(de.pflegital.chatbot.model.ConversationPartner.ANGEHOERIGE_PERSON);
 
            // Zeitraum
            Period period = new Period();
            period.setCareStart(LocalDate.now().plusDays(1));
            period.setCareEnd(LocalDate.now().plusDays(10));
            formData.setCarePeriod(period);
 
            // Adresse für alle Beteiligten
            de.pflegital.chatbot.model.Address address = new de.pflegital.chatbot.model.Address();
            address.setStreet("Musterstraße");
            address.setHouseNumber(1);
            address.setCity("Musterstadt");
            address.setZip("12345");
 
            // Caregiver
            Caregiver caregiver = new Caregiver();
            caregiver.setRegularCaregiverName("Max Mustermann");
            caregiver.setRegularCaregiverAddress(address);
            caregiver.setRegularCareStartedDate(LocalDate.now().minusYears(2));
            caregiver.setRegularCaregiverPhoneNumber("+491234567890");
            formData.setCaregiver(caregiver);
            System.out.println(caregiver);
 
            // CareRecipient
            de.pflegital.chatbot.model.Carerecipient careRecipient = new de.pflegital.chatbot.model.Carerecipient();
            careRecipient.setFullName("Erika Musterfrau");
            careRecipient.setBirthDate(LocalDate.now().minusYears(80));
            careRecipient.setInsuredAddress(address);
            careRecipient.setPhoneNumber("+491234567891");
            careRecipient.setInsuranceNumber("12345678901");
            formData.setCareRecipient(careRecipient);
 
            // ReplacementCare (private Person)
            de.pflegital.chatbot.model.ReplacementCare replacementCare = new de.pflegital.chatbot.model.ReplacementCare();
            replacementCare.setIsProfessional(true);
         /*    de.pflegital.chatbot.model.replacementcare.PrivatePerson privatePerson = new de.pflegital.chatbot.model.replacementcare.PrivatePerson(); */
         /*    privatePerson.setPrivatePersonName("Anna Ersatz");
            privatePerson.setPrivatePersonAddress(address);
            privatePerson.setPrivatePersonPhone("+491234567892"); */
           /*  privatePerson.setRelative(true);
            privatePerson.setSameHousehold(false);
            privatePerson.setHasExpenses(false);
            replacementCare.setPrivatePerson(privatePerson); */
            formData.setReplacementCare(replacementCare);
 
            formData.setChatbotMessage("✅ Danke! Es wurden alle benötigten Informationen gesammelt!");
 
            // Prozess starten:
            startBpmnProcess(formData, waId);
            updatedResponse = formData;
        }
        sessionStore.setFormData(waId, updatedResponse);
 
        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(waId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    public void startBpmnProcess(FormData finalFormData, String waId) {
        Client client = ClientBuilder.newClient();
 
        try {
            WebTarget target = client.target("http://localhost:8083/formDataProcess");
            Map<String, Object> requestBody = Map.of(
                    "message", finalFormData,
                    "waId", waId);
 
            try (Response response = target.request()
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))) {
 
                int status = response.getStatus();
                if (status != 200 && status != 201) {
                    LOG.error("Prozessstart fehlgeschlagen. Status: {}", response.getStatus());
                }
                LOG.info("BPMN-Prozess erfolgreich gestartet für WAID: {}", waId);
            }
        } catch (Exception e) {
            LOG.error("Fehler beim Aufruf des BPMN-Prozesses", e);
        } finally {
            client.close();
        }
    }
}
 