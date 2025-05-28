package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.slf4j.Logger;

@ApplicationScoped
public class Pflegebot {
    @Inject
    SessionStore sessionStore;

    @Inject
    AiService aiService;

    @Inject
    FormDataPresenter formDataPresenter;

    private final Logger LOG = org.slf4j.LoggerFactory.getLogger(Pflegebot.class);

    public ChatResponse processUserInput(String sessionId, String userInput) {
        FormData session = sessionStore.getFormData(sessionId);
        LOG.info("SessionID: {}",session);
        if (session == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }

        LOG.info("User writes: {}", userInput);

        // Prompt bauen mit aktuellem Zustand
        String jsonFormData = formDataPresenter.present(session);
        String prompt = "The current form data is: " + jsonFormData + ". The user just said: '" + userInput + "'. Please update the missing fields accordingly.";

        LOG.info("Prompt to AI: {}", prompt);
        FormData updatedResponse = aiService.chatWithAiStructured(prompt);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage("Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        }

        // Wenn vollständig: andere Antwort setzen
        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Thank you! All required information has been collected.");
            // FIXME: Start process here
        }
        sessionStore.setFormData(sessionId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(sessionId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
