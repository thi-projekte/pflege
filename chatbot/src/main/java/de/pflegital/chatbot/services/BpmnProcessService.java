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
import de.pflegital.chatbot.exception.BpmnProcessException;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class BpmnProcessService {

    private static final Logger LOG = getLogger(BpmnProcessService.class);
    private static final String PROCESS_API_URL_PROD = "https://pflege-prozess.winfprojekt.de/formDataProcess";

    public void startBpmnProcess(FormData finalFormData, String waId) {
        Client client = ClientBuilder.newClient();

        try {
            WebTarget target = client.target(PROCESS_API_URL_PROD);
            Map<String, Object> requestBody = Map.of(
                    "message", finalFormData,
                    "waId", waId);

            post(target, requestBody, LOG);
        } catch (Exception e) {
            throw new BpmnProcessException("Fehler beim Aufruf des BPMN-Prozesses für waId: " + waId, e);
        } finally {
            client.close();
        }
    }

    public static void post(WebTarget target, Map<String, Object> requestBody, Logger log) {
        try (Response response = target.request()
                .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))) {

            int status = response.getStatus();
            if (status != 200 && status != 201) {
                log.error("Prozessstart fehlgeschlagen. Status: {}", response.getStatus());
            }
            log.info("BPMN-Prozess erfolgreich gestartet");
        }
    }
}
