package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;

@ApplicationScoped
public class Pflegebot {
    @Inject
    SessionStore sessionStore;

    @Inject
    AiService aiService;

    @Inject
    FormDataPresenter formDataPresenter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Logger LOG = org.slf4j.LoggerFactory.getLogger(Pflegebot.class);

    public ChatResponse processUserInput(String sessionId, String userInput) {
        LOG.info("IM PFLEGEBOT: {}", sessionId);
        if (sessionStore.getFormData(sessionId) == null) {
            FormData aiResponse = new FormData();
            sessionStore.setFormData(sessionId, aiResponse);
        }
        FormData session = sessionStore.getFormData(sessionId);
        LOG.info("SessionID: {}", session);
        if (session == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }

        LOG.info("User writes: {}", userInput);

        // Prompt bauen mit aktuellem Zustand
        String jsonFormData = formDataPresenter.present(session);
        String prompt = "The current form data is: " + jsonFormData + ". The user just said: '" + userInput
                + "'. Please update the missing fields accordingly.";

        LOG.info("Prompt to AI: {}", prompt);
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        FormData updatedResponse = aiService.chatWithAiStructured(sessionId, prompt, currentDate);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        }

        // Wenn vollständig: andere Antwort setzen
        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Danke! Es wurden alle benötigten Informationen gesammelt!");
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
