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

        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Danke! Es wurden alle benötigten Informationen gesammelt!");
            // Prozess starten:
            startBpmnProcess(updatedResponse, waId);
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
                "waId", waId
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
