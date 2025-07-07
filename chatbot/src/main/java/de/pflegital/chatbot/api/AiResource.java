/*
 * This class is only used for testing the ai service without whatsapp integration.
 * It is not used in the production environment.
 */
package de.pflegital.chatbot.api;

import de.pflegital.chatbot.services.BpmnProcessService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;

import de.pflegital.chatbot.FormData;
import de.pflegital.chatbot.SessionStore;
import de.pflegital.chatbot.exception.BpmnProcessException;
import de.pflegital.chatbot.exception.ChatResponseCreationException;
import de.pflegital.chatbot.model.ChatResponse;
import de.pflegital.chatbot.services.AiService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import de.pflegital.chatbot.tools.FormDataCompleted;

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
    ChatMemoryStore chatMemoryStore;

    @Inject
    SessionStore sessionStore;

    @Inject
    FormDataCompleted formDataCompleted;

    private static final Logger LOG = getLogger(AiResource.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String CURRENT_DATE = LocalDate.now().format(DATE_FORMATTER);

    @POST
    @Path("/start")
    public ChatResponse startChat() {
        String memoryId = UUID.randomUUID().toString();
        FormData aiResponse = aiService.chatWithAiStructured(memoryId, "Start conversation.", CURRENT_DATE);
        sessionStore.setFormData(memoryId, aiResponse);

        try {
            LOG.info("Chat started: {}", aiResponse.getChatbotMessage());
            return new ChatResponse(memoryId, aiResponse);
        } catch (Exception e) {
            throw new ChatResponseCreationException("Fehler beim Erstellen der ChatResponse für memoryId: " + memoryId,
                    e);
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
        FormData updatedResponse = getFormData(memoryId, userInput);

        String validation = formDataCompleted.checkFormData(updatedResponse);
        if ("Alle erforderlichen Felder sind ausgefüllt und gültig!".equals(validation)) {
            updatedResponse.setChatbotMessage("Danke! Es wurden alle benötigten Informationen gesammelt!");
            // Prozess starten:
            startBpmnProcess(updatedResponse, memoryId);
        }
        sessionStore.setFormData(memoryId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(memoryId, updatedResponse);
        } catch (Exception e) {
            throw new ChatResponseCreationException("Fehler beim Erstellen der ChatResponse für memoryId: " + memoryId,
                    e);
        }
    }

    @Retry(maxRetries = 3)
    protected FormData getFormData(String memoryId, String prompt) {
        LOG.info("Prompt to AI: {}", prompt);
        try {
            return aiService.chatWithAiStructured(memoryId, prompt, CURRENT_DATE);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    // local bpmn process
    public void startBpmnProcess(FormData finalFormData, String waId) {
        Client client = ClientBuilder.newClient();

        try {
            WebTarget target = client.target("http://localhost:8083/formDataProcess");
            Map<String, Object> requestBody = Map.of(
                    "message", finalFormData,
                    "waId", waId,
                    "waid", waId);

            try {
                BpmnProcessService.post(waId, target, requestBody, LOG);
            } catch (BpmnProcessException e) {
                throw new BpmnProcessException("Fehler beim BPMN-Prozess für waId: " + waId, e);
            }
        } finally {
            client.close();
        }
    }
}
