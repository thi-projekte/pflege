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

    public ChatResponse processUserInput(String sessionId, String userInput) {
        LOG.info("IM PFLEGEBOT: {}", sessionId);
        if (sessionStore.getFormData(sessionId) == null) {
            FormData aiResponse = new FormData();
            sessionStore.setFormData(sessionId, aiResponse);
        }
        FormData session = sessionStore.getFormData(sessionId);
        LOG.info("SessionID: {}", session);
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
        FormData updatedResponse = aiService.chatWithAiStructured(sessionId, prompt, currentDate);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        }

        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Danke! Es wurden alle benötigten Informationen gesammelt!");
            // Prozess starten:
            startBpmnProcess(updatedResponse);
        }
        sessionStore.setFormData(sessionId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(sessionId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startBpmnProcess(FormData finalFormData) {
        Client client = ClientBuilder.newClient();

        try {
            WebTarget target = client.target("http://localhost:8083/formDataProcess");
            Map<String, Object> requestBody = Map.of("message", finalFormData);

            try (Response response = target.request()
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))) {

                int status = response.getStatus();
                if (status != 200 && status != 201) {
                    LOG.error("Prozessstart fehlgeschlagen. Status: {}", response.getStatus());
                }
                LOG.info("BPMN-Prozess erfolgreich gestartet");
            }
        } catch (Exception e) {
            LOG.error("Fehler beim Aufruf des BPMN-Prozesses", e);
        } finally {
            client.close();
        }
    }
}
