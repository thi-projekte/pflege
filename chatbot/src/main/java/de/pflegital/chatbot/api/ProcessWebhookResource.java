package de.pflegital.chatbot.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pflegital.chatbot.exception.WhatsAppApiException;
import de.pflegital.chatbot.model.ChatbotRequest;
import de.pflegital.chatbot.services.ProcessRequestAiService;
import de.pflegital.chatbot.services.WhatsAppRestClient;

@Path("/process-webhook")
public class ProcessWebhookResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessWebhookResource.class);

    @Inject
    ProcessRequestAiService processRequestAiService;

    @Inject
    WhatsAppRestClient whatsAppRestClient;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String callChatbot(ChatbotRequest request) {
        try {
            LOG.info("Processing request: {}", request.getRequest());
            String response = processRequestAiService.processRequest(request.getRequest());
            LOG.info("AI response: {}", response);

            sendWhatsAppReplySafe(request.getWhatsAppNumber(), response);

            return response;
        } catch (Exception e) {
            throw new WebApplicationException("Error processing request", e);
        }
    }

    private void sendWhatsAppReplySafe(String number, String response) {
        try {
            whatsAppRestClient.sendWhatsAppReply(number, response);
            LOG.info("WhatsApp message sent to: {}", number);
        } catch (Exception e) {
            throw new WhatsAppApiException("Error sending WhatsApp message to: " + number, e);
        }
    }
}
