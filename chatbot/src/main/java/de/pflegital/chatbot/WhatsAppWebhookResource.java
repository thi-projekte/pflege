package de.pflegital.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//import io.netty.channel.ChannelOutboundBuffer.MessageProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.pflegital.chatbot.ChatResponse;

@Path("/webhook")
@ApplicationScoped
public class WhatsAppWebhookResource {

    private static final Logger LOGGER = Logger.getLogger(WhatsAppWebhookResource.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ConfigProperty(name = "whatsapp.verify.token")
    String configuredVerifyToken;

    @ConfigProperty(name = "whatsapp.api.token")
    String whatsappApiToken;

    @ConfigProperty(name = "whatsapp.phone.number.id")
    String whatsappPhoneNumberId;

    @ConfigProperty(name = "whatsapp.api.version")
    String whatsappApiVersion;

    //@Inject
    //MessageProcessor messageProcessor; // Injizierte Instanz von Datei 2

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyWebhook(
            @QueryParam("hub.mode") String mode,
            @QueryParam("hub.verify_token") String tokenFromHub,
            @QueryParam("hub.challenge") String challenge) {

        LOGGER.info("GET /webhook - Verification attempt.");
        LOGGER.fine("Mode: " + mode + ", Token: " + tokenFromHub + ", Challenge: " + challenge);

        if ("subscribe".equals(mode) && configuredVerifyToken.equals(tokenFromHub)) {
            LOGGER.info("Webhook VERIFIED successfully!");
            return Response.ok(challenge).build();
        } else {
            LOGGER.warning("Webhook verification FAILED. Mode: " + mode +
                           ", Provided Token: " + tokenFromHub +
                           ", Expected Token: " + configuredVerifyToken);
            return Response.status(Response.Status.FORBIDDEN).entity("Webhook verification failed").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) // Wichtig: WhatsApp erwartet eine 200 OK mit JSON Body oder leer
    public Response handleIncomingMessage(String requestBody) {
        LOGGER.info("POST /webhook - Received payload: " + requestBody);

        try {
            JsonNode rootNode = objectMapper.readTree(requestBody);

            if (rootNode.has("object") && "whatsapp_business_account".equals(rootNode.get("object").asText())) {
                JsonNode entries = rootNode.path("entry");
                for (JsonNode entry : entries) {
                    JsonNode changes = entry.path("changes");
                    for (JsonNode change : changes) {
                        if ("messages".equals(change.path("field").asText())) {
                            JsonNode value = change.path("value");
                            JsonNode messages = value.path("messages");
                            for (JsonNode messageNode : messages) {
                                if (messageNode.has("type") && "text".equals(messageNode.path("type").asText())) {
                                    String fromWaid = messageNode.path("from").asText();
                                    String messageText = messageNode.path("text").path("body").asText();

                                    LOGGER.info("Extracted Text Message from " + fromWaid + ": " + messageText);

                                    // --- Vorbereitung der Übergabe an Datei 2 ---
                                    // `fromWaid` und `messageText` sind bereits vorbereitet.

                                    // --- Übergabe an Datei 2 und Empfang der Antwort ---
                                    AiResource chatResponse = new AiResource();
                                    String replyText = chatResponse.processUserInput(fromWaid, messageText).getMessage();


                                    // --- Senden der Antwort zurück an WhatsApp ---
                                    if (replyText != null && !replyText.isEmpty()) {
                                        sendWhatsAppReply(fromWaid, replyText);
                                    }
                                } else if (messageNode.has("type")) {
                                    LOGGER.info("Received non-text message (type: " +
                                            messageNode.path("type").asText() + "). Ignoring.");
                                } else {
                                    LOGGER.warning("Received message without a type field. Payload: " + messageNode.toString());
                                }
                            }
                        }
                    }
                }
            }
            // Erfolgreiche Verarbeitung, sende 200 OK
            return Response.ok("{\"status\":\"EVENT_RECEIVED\"}").build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing incoming WhatsApp message: " + e.getMessage(), e);
            // Sende einen Fehlerstatus, damit WhatsApp nicht ständig versucht, die Nachricht erneut zu senden.
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"status\":\"ERROR_PROCESSING_EVENT\", \"message\":\"" + e.getMessage() + "\"}")
                           .build();
        }
    }

    private void sendWhatsAppReply(String recipientWaid, String messageText) {
        try {
            String escapedMessageText = escapeJson(messageText); // Wichtig für JSON-Sicherheit
            String jsonPayload = String.format("""
                {
                    "messaging_product": "whatsapp",
                    "to": "%s",
                    "type": "text",
                    "text": {
                        "preview_url": false,
                        "body": "%s"
                    }
                }
                """, recipientWaid, escapedMessageText);

            HttpClient client = HttpClient.newHttpClient(); // Kann auch als Bean für bessere Testbarkeit injiziert werden
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://graph.facebook.com/" + whatsappApiVersion + "/" + whatsappPhoneNumberId + "/messages"))
                    .header("Authorization", "Bearer " + whatsappApiToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            LOGGER.info("WhatsApp reply API call to " + recipientWaid + ". Status: " + response.statusCode());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOGGER.fine("WhatsApp reply API response body: " + response.body());
            } else {
                LOGGER.warning("Error sending WhatsApp reply. Status: " + response.statusCode() + ", Body: " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Exception sending WhatsApp reply to " + recipientWaid + ": " + e.getMessage(), e);
            Thread.currentThread().interrupt(); // Gute Praxis bei InterruptedException
        }
    }

    // Einfache JSON String Escaping Funktion
    private String escapeJson(String s) {
        if (s == null) {
            return ""; // Oder null, je nachdem wie die API leere Strings behandelt
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}