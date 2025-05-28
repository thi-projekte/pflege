package de.pflegital.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    AiService AiService;
    @Inject
    Pflegebot pflegebot;

    /**
     * Verifiziert den Webhook durch Vergleich des Tokens mit dem konfigurierten.
     */
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

    /**
     * Hauptlogik zur Verarbeitung eingehender WhatsApp-Nachrichten.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleIncomingMessage(String requestBody) {
        LOGGER.info("Received WhatsApp message: " + requestBody);

        try {
            JsonNode rootNode = objectMapper.readTree(requestBody);

            if (rootNode.has("object") && "whatsapp_business_account".equals(rootNode.get("object").asText())) {
                for (JsonNode entry : rootNode.path("entry")) {
                    for (JsonNode change : entry.path("changes")) {
                        if ("messages".equals(change.path("field").asText())) {
                            for (JsonNode messageNode : change.path("value").path("messages")) {
                                if ("text".equals(messageNode.path("type").asText())) {

                                    String fromWaid = messageNode.path("from").asText();
                                    String messageText = messageNode.path("text").path("body").asText();

                                    LOGGER.info("Message from " + fromWaid + ": " + messageText);

                                    //String sessionId = whatsAppClient.startSession();
                                    pflegebot.processUserInput(fromWaid,messageText);
                                    /*if (sessionId != null) {
                                        String replyText = whatsAppClient.sendToReply(sessionId, messageText);
                                        if (replyText != null && !replyText.isEmpty()) {
                                            whatsAppClient.sendWhatsAppReply(fromWaid, replyText);
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                }
            }

            return Response.ok("{\"status\":\"EVENT_RECEIVED\"}").build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling WhatsApp message", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"ERROR_PROCESSING_EVENT\"}")
                    .build();
        }
    }
}
