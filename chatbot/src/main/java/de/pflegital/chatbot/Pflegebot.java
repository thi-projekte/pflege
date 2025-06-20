package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;


@ApplicationScoped
public class Pflegebot {
    @Inject
    AiService aiService;

    @Inject
    SessionStore sessionStore;

    private static final Logger LOG = getLogger(Pflegebot.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String currentDate = LocalDate.now().format(DATE_FORMATTER);


    public ChatResponse processUserInput(String waId, String userInput) {
        FormData currentFormData = sessionStore.getFormData(waId);
        if (currentFormData == null) {
            FormData aiResponse = new FormData();
            sessionStore.setFormData(waId, aiResponse);
        } 

        FormData updatedResponse = aiService.chatWithAiStructured(waId, userInput, currentDate);

        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("✅ Danke! Es wurden alle benötigten Informationen gesammelt! Ich werde Sie über die nächsten Schritte informieren, sobald es möglich ist.");
            startBpmnProcess(updatedResponse, waId);
        }
        sessionStore.setFormData(waId, updatedResponse);

        try {
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
