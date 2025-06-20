package de.pflegital.chatbot;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {
    @Inject
    AiService aiService;


    @Inject
    WhatsAppRestClient whatsAppRestClient;

    @Inject
    ProcessRequestAiService processRequestAiService;

    @Inject
    ChatMemoryStore chatMemoryStore;

    private static final Logger LOG = getLogger(AiResource.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String currentDate = LocalDate.now().format(DATE_FORMATTER);
    @Inject
    SessionStore sessionStore;

    @POST
    @Path("/start")
    public ChatResponse startChat() {
        String memoryId = UUID.randomUUID().toString();
        FormData aiResponse = aiService.chatWithAiStructured(memoryId, "Start conversation.", currentDate);
        sessionStore.setFormData(memoryId, aiResponse);

        try {
            LOG.info("Chat started: {}", aiResponse.getChatbotMessage());
            return new ChatResponse(memoryId, aiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/callChatbot")
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

    @POST
    @Path("/reply")
    public ChatResponse processUserInput(@QueryParam("memoryId") String memoryId, String userInput) {
        FormData currentFormData = sessionStore.getFormData(memoryId);
        if (currentFormData == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }
        LOG.info("User writes: {}", userInput);

        List<ChatMessage> memory = chatMemoryStore.getMessages(memoryId);
        LOG.info("Memory has {} items", memory.size());
        FormData updatedResponse = getFormData(memoryId, userInput, currentDate);

        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Danke! Es wurden alle benötigten Informationen gesammelt!");
            // Prozess starten:
            startBpmnProcess(updatedResponse, memoryId);
        }
        sessionStore.setFormData(memoryId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(memoryId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Retry(maxRetries = 3)
    protected FormData getFormData(String memoryId, String prompt, String currentDate) {
        LOG.info("Prompt to AI: {}", prompt);
        try {
            return aiService.chatWithAiStructured(memoryId, prompt, currentDate);
        } catch (Exception e) {
            LOG.error("Error getting form data: {}", e.getMessage());
            throw new WebApplicationException(e);
        }
    }

    public void startBpmnProcess(FormData finalFormData, String waId) {
        Client client = ClientBuilder.newClient();

        try {
            WebTarget target = client.target("http://localhost:8083/formDataProcess");
            Map<String, Object> requestBody = Map.of(
                    "message", finalFormData,
                    "waId", waId,
                    "waid", waId);

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
