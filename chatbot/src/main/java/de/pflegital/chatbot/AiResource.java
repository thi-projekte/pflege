package de.pflegital.chatbot;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;

import de.pflegital.chatbot.model.Address;
import de.pflegital.chatbot.model.ReplacementCare;
import de.pflegital.chatbot.model.replacementcare.PrivatePerson;
import de.pflegital.chatbot.tools.InsuranceNumberTool;
import io.quarkus.security.Authenticated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {
    @Inject
    AiService aiService;

    @Inject
    FormDataPresenter formDataPresenter;

    @Inject
    InsuranceNumberTool insuranceNumberTool;

    private final Map<String, FormData> sessions = new HashMap<>();
    private static final Logger LOG = getLogger(AiResource.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Inject
    SessionStore sessionStore;

    @POST
    @Path("/start")
    public ChatResponse startChat() {
        String sessionId = UUID.randomUUID().toString();

        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        FormData aiResponse = aiService.chatWithAiStructured(sessionId, "Start conversation.", currentDate);
        sessionStore.setFormData(sessionId, aiResponse);

        try {
            LOG.info("Chat started: {}", aiResponse.getChatbotMessage());
            return new ChatResponse(sessionId, aiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/reply")
    public ChatResponse processUserInput(@QueryParam("sessionId") String sessionId, String userInput) {
        FormData session = sessionStore.getFormData(sessionId);
        if (session == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }

        LOG.info("User writes: {}", userInput);

        // Prompt bauen mit aktuellem Zustand
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

        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        FormData updatedResponse = getFormData(sessionId, prompt, currentDate);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        }

        // Wenn vollständig: andere Antwort setzen
        if (!updatedResponse.isComplete()) {
            Address adress = new Address();
            adress.setCity("city");
            adress.setHouseNumber(1);
            adress.setStreet("street");
            adress.setZip("12345");
            
            PrivatePerson privatePerson = new PrivatePerson();
            privatePerson.setHasExpenses(true);
            privatePerson.setPrivatePersonName("name");
            privatePerson.setPrivatePersonAddress(adress);
            privatePerson.setPrivatePersonPhone("01573232312");
            privatePerson.setRelative(true);
            privatePerson.setSameHousehold(true);
            
            ReplacementCare replacementCare = new ReplacementCare();
            replacementCare.setPrivatePerson(privatePerson);
            replacementCare.setIsProfessional(false);
            
            updatedResponse.setReplacementCare(replacementCare);
            updatedResponse.setChatbotMessage("Danke! Es wurden alle benötigten Informationen gesammelt!");
            startBpmnProcess(updatedResponse, "<12345678819>");
        }
        sessionStore.setFormData(sessionId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(sessionId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Retry(maxRetries = 3)
    protected FormData getFormData(String sessionId, String prompt, String currentDate) {
        LOG.info("Prompt to AI: {}", prompt);
        try {
            return aiService.chatWithAiStructured(sessionId, prompt, currentDate);
        } catch (Exception e) {
            LOG.error("Error getting form data: {}", e.getMessage());
            throw new WebApplicationException(e);
        }
    }

    public void startBpmnProcess(FormData finalFormData, String waId) {
        Client client = ClientBuilder.newClient();

        try {
            WebTarget target = client.target("http://localhost:8083/formDataProcess");
            Map<String, Object> requestBody = Map.of(
                "message", finalFormData,
                "waId", waId,
                "waid", waId
            );

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
