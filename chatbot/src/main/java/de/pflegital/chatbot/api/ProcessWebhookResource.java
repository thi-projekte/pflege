package de.pflegital.chatbot.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            try {
                whatsAppRestClient.sendWhatsAppReply(request.getWhatsAppNumber(), response);
                LOG.info("WhatsApp message sent to: {}", request.getWhatsAppNumber());
            } catch (Exception e) {
                LOG.error("Error sending WhatsApp message: {}", e.getMessage());
            }

            return response;
        } catch (Exception e) {
            LOG.error("Error processing request: {}", e.getMessage());
            throw new WebApplicationException("Error processing request", e);
        }
    }
}
