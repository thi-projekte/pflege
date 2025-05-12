package de.pflegital.chatbot.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class WhatsAppService {

    private static final Logger LOG = getLogger(WhatsAppService.class);

    @Inject
    @RestClient // Injiziert den REST Client für die Graph API
    WhatsAppClient whatsAppClient;

    @ConfigProperty(name = "pflegital.whatsapp.graph-api-token")
    String graphApiToken;

    @ConfigProperty(name = "pflegital.whatsapp.phone-number-id")
    String phoneNumberId;

    public void sendTextMessage(String recipientWaId, String messageBody) {
        if (recipientWaId == null || messageBody == null || messageBody.isBlank()) {
            LOG.error("Ungültige Parameter zum Senden einer WhatsApp-Nachricht: recipientWaId={}, messageBody='{}'", recipientWaId, messageBody);
            return;
        }
        String bearerToken = "Bearer " + graphApiToken;
        SendMessageRequest request = new SendMessageRequest(recipientWaId, new TextMessage(messageBody));

        try {
            LOG.info("Sende Nachricht an {}: '{}'", recipientWaId, messageBody);
            Response response = whatsAppClient.sendMessage(phoneNumberId, bearerToken, request);
            try { // Sicherstellen, dass die Response geschlossen wird
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    LOG.info("Nachricht erfolgreich an {} gesendet. Status: {}", recipientWaId, response.getStatus());
                } else {
                    String errorBody = response.readEntity(String.class);
                    LOG.error("Fehler beim Senden der Nachricht an {}. Status: {}. Body: {}",
                            recipientWaId, response.getStatus(), errorBody);
                }
            } finally {
                response.close();
            }
        } catch (WebApplicationException e) { // Spezifischer Fehler vom REST Client
             Response errorResponse = e.getResponse();
             String errorBody = "(Keine Fehlerdetails lesbar)";
             if (errorResponse != null && errorResponse.hasEntity()) {
                try {
                    errorBody = errorResponse.readEntity(String.class);
                } catch (Exception readEx) {
                    LOG.warn("Konnte Fehlerdetails nicht aus der Response lesen.", readEx);
                } finally {
                    errorResponse.close();
                }
             }
            LOG.error("WebApplicationException beim Senden an {}: Status={}, Body={}, Message={}",
                    recipientWaId, errorResponse != null ? errorResponse.getStatus() : "N/A", errorBody, e.getMessage(), e);
        }
        catch (Exception e) { // Allgemeiner Fehler
            LOG.error("Allgemeiner Fehler bei der Kommunikation mit der WhatsApp Graph API für {}: {}", recipientWaId, e.getMessage(), e);
        }
    }

    // --- Interne DTOs für das Senden ---
    static class SendMessageRequest {
        @JsonProperty("messaging_product")
        String messagingProduct = "whatsapp";
        String to;
        String type = "text";
        TextMessage text;

        SendMessageRequest(String to, TextMessage text) {
            this.to = to;
            this.text = text;
        }
    }

    static class TextMessage {
        @JsonProperty("preview_url")
        boolean previewUrl = false; // Setze true, wenn Link-Vorschau gewünscht
        String body;

        TextMessage(String body) {
            this.body = body;
        }
    }
}