package de.pflegital.chatbot.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.pflegital.chatbot.Pflegebot;
import de.pflegital.chatbot.exception.WhatsAppApiException;
import de.pflegital.chatbot.model.ChatResponse;
import de.pflegital.chatbot.services.AiService;
import de.pflegital.chatbot.services.WhatsAppRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

@Path("/webhook")
@ApplicationScoped
public class WhatsAppWebhookResource {

    private static final Logger LOGGER = Logger.getLogger(WhatsAppWebhookResource.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ConfigProperty(name = "whatsapp.verify.token")
    String configuredVerifyToken;

    @Inject
    WhatsAppRestClient whatsAppClient;

    @Inject
    AiService aiService;

    @Inject
    Pflegebot pflegebot;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyWebhook(
            @QueryParam("hub.mode") String mode,
            @QueryParam("hub.verify_token") String tokenFromHub,
            @QueryParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && configuredVerifyToken.equals(tokenFromHub)) {
            LOGGER.info("Webhook VERIFIED successfully!");
            return Response.ok(challenge).build();
        } else {
            LOGGER.warning("Webhook verification FAILED.");
            return Response.status(Response.Status.FORBIDDEN).entity("Webhook verification failed").build();
        }
    }

    public Response handleIncomingMessage(String requestBody) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(String.format("Received Whatsapp message: %s", requestBody));
        }

        try {
            JsonNode rootNode = objectMapper.readTree(requestBody);
            processWebhookEntries(rootNode); // Delegate processing
            return Response.ok("{\"status\":\"EVENT_RECEIVED\"}").build();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling WhatsApp message", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"ERROR_PROCESSING_EVENT\"}")
                    .build();
        }
    }

    private void processWebhookEntries(JsonNode rootNode) {
        if (!isValidWhatsAppObject(rootNode)) {
            return;
        }
        StreamSupport.stream(rootNode.path("entry").spliterator(), false)
                .flatMap(entry -> StreamSupport.stream(entry.path("changes").spliterator(), false))
                .filter(change -> "messages".equals(change.path("field").asText()))
                .flatMap(change -> StreamSupport.stream(change.path("value").path("messages").spliterator(), false))
                .forEach(this::processMessage);
    }

    private boolean isValidWhatsAppObject(JsonNode rootNode) {
        return rootNode.has("object") && "whatsapp_business_account".equals(rootNode.get("object").asText());
    }

    private void processMessage(JsonNode messageNode) {
        if ("text".equals(messageNode.path("type").asText())) {
            String fromWaid = messageNode.path("from").asText();
            String messageText = messageNode.path("text").path("body").asText();

            LOGGER.log(Level.INFO, "Message from {0}: {1}", new Object[] { fromWaid, messageText });

            ChatResponse reply = pflegebot.processUserInput(fromWaid, messageText);

            if (!reply.getMessage().isEmpty()) {
                sendWhatsAppReply(fromWaid, reply.getMessage()); // Delegate sending the reply
            }
        }
    }

    private void sendWhatsAppReply(String recipientId, String message) {
        try {
            LOGGER.log(Level.INFO, "Sending reply to {0}: {1}", new Object[] { recipientId, message });
            whatsAppClient.sendWhatsAppReply(recipientId, message);
        } catch (WhatsAppApiException e) {
            LOGGER.log(Level.SEVERE, "Error sending WhatsApp message to {0}: {1}",
                    new Object[] { recipientId, e.getMessage() });
        }
    }
}
