package de.pflegital.chatbot.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;

import de.pflegital.chatbot.FormData;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class BpmnProcessService {

    private static final Logger LOG = getLogger(BpmnProcessService.class);

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