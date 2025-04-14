package org;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {

    @Inject
    AiService aiService;

    private final Map<String, FormData> sessions = new HashMap<>();

    @POST
    @Path("/start")
    public ChatResponse startChat() {
        String sessionId = UUID.randomUUID().toString();
        FormData aiResponse = aiService.chatWithAiStructured("Start conversation.");
        sessions.put(sessionId, aiResponse);

        try {
            System.out.println("Chat started:" + aiResponse.getChatbotMessage());
            return new ChatResponse(sessionId,aiResponse);
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

        System.out.println("User writes: " + userInput);

        // Prompt bauen mit aktuellem Zustand
        String prompt = "The current form data is: " + session.toString() +
                ". The user just said: '" + userInput + "'. Please update the missing fields accordingly.";
System.out.println(prompt);
        FormData updatedResponse = aiService.chatWithAiStructured(prompt);

        // Wenn vollständig: andere Antwort setzen
        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Thank you! All required information has been collected.");
        }
        sessions.put(sessionId, updatedResponse);

        try {
            System.out.println("AI response: " + updatedResponse.getChatbotMessage());
           return new ChatResponse(sessionId, updatedResponse);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }
}