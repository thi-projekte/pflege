package de.pflegital.chatbot;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;

import de.pflegital.chatbot.tools.InsuranceNumberTool;
import io.quarkus.security.Authenticated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    FormDataPresenter formDataPresenter;

    @Inject
    InsuranceNumberTool insuranceNumberTool;

    private final Map<String, FormData> sessions = new HashMap<>();
    private static final Logger LOG = getLogger(AiResource.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Inject
    SessionStore sessionStore;

    @POST
    @Path("/start")
    public ChatResponse startChat() {
        String sessionId = UUID.randomUUID().toString();

        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        FormData aiResponse = aiService.chatWithAiStructured(sessionId, "Start conversation.", currentDate);
        sessionStore.setFormData(sessionId, aiResponse);

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
        FormData session = sessionStore.getFormData(sessionId);
        if (session == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }

        LOG.info("User writes: {}", userInput);

        // Prompt bauen mit aktuellem Zustand
        String jsonFormData = formDataPresenter.present(session);
        String prompt = """
                CONTEXT:
                The user input is:
                %s

                USER INPUT:
                %s

                INSTRUCTION:
                - Analyze the user input in the context of the above form data.
                - Only ask for or correct information that is missing or invalid.
                - Do not repeat or overwrite valid information.
                - Respond ONLY with updated form data in JSON format.
                """.formatted(jsonFormData, userInput);

        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        FormData updatedResponse = getFormData(sessionId, prompt, currentDate);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
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

    @Retry(maxRetries = 3)
    protected FormData getFormData(String sessionId, String prompt, String currentDate) {
        LOG.info("Prompt to AI: {}", prompt);
        try {
            return aiService.chatWithAiStructured(sessionId, prompt, currentDate);
        } catch (Exception e) {
            LOG.error("Error getting form data: {}", e.getMessage());
            throw new WebApplicationException(e);
        }
    }
}
