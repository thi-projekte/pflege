package org;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import io.quarkus.security.Authenticated;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class AiResource {
    @Inject
    AiService aiService;

    @Inject
    FormDataPresenter formDataPresenter;

    private final Map<String, FormData> sessions = new HashMap<>();
    private static final Logger LOG = getLogger(AiResource.class);

    @POST
    @Path("/start")
    public ChatResponse startChat() {
        String sessionId = UUID.randomUUID().toString();
        FormData aiResponse = aiService.chatWithAiStructured("Start conversation.");
        sessions.put(sessionId, aiResponse);

        try {
            LOG.info("Chat started: {}", aiResponse.getChatbotMessage());
            return new ChatResponse(sessionId, aiResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/reply")
    public ChatResponse processUserInput(@QueryParam("sessionId") String sessionId, String userInput) {
        FormData session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }

        LOG.info("User writes: {}", userInput);

        // Prompt bauen mit aktuellem Zustand
        String jsonFormData = formDataPresenter.present(session);
        String prompt = "The current form data is: " + jsonFormData +
                ". The user just said: '" + userInput + "'. Please update the missing fields accordingly.";


        LOG.info("Prompt to AI: {}", prompt);
        FormData updatedResponse = aiService.chatWithAiStructured(prompt);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        }

        // Wenn vollständig: andere Antwort setzen
        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Thank you! All required information has been collected.");
            //FIXME: Start process here
        }
        sessions.put(sessionId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(sessionId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}