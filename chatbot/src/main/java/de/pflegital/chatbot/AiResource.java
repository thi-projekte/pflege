package de.pflegital.chatbot;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Retry;
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

    private final AiService aiService;
    private final FormDataPresenter formDataPresenter;
    private final InsuranceNumberTool insuranceNumberTool;
    private final Map<String, FormData> sessions = new HashMap<>();
    private static final Logger LOG = getLogger(AiResource.class);

    @Inject
    public AiResource(
            AiService aiService,
            FormDataPresenter formDataPresenter,
            InsuranceNumberTool insuranceNumberTool) {
        this.aiService = aiService;
        this.formDataPresenter = formDataPresenter;
        this.insuranceNumberTool = insuranceNumberTool;
    }

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
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("/reply")
    public ChatResponse processUserInput(@QueryParam("sessionId") String sessionId, String userInput) {
        FormData session = sessions.get(sessionId);
        if (session == null) {
            throw new NotAuthorizedException("Sie sind nicht authorisiert.");
        }

        LOG.info("User writes: {}", userInput);

        // Prompt bauen mit aktuellem Zustand
        String jsonFormData = formDataPresenter.present(session);
        String prompt = "The current form data is: " + jsonFormData +
                ". The user just said: '" + userInput + "'. Please update the missing fields accordingly.";

        FormData updatedResponse = getFormData(prompt);

        if (updatedResponse.getCareLevel() != null && updatedResponse.getCareLevel() < 2) {
            updatedResponse.setChatbotMessage(
                    "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
        } else if (updatedResponse.getCareRecipient() != null &&
                updatedResponse.getCareRecipient().getInsuranceNumber() != null &&
                !insuranceNumberTool.isValidSecurityNumber(updatedResponse.getCareRecipient().getInsuranceNumber())) {
            updatedResponse.setChatbotMessage(
                    "Die angegebene Versicherungsnummer scheint ungültig zu sein. Bitte überprüfen Sie Ihre Eingabe.");
        }

        // Wenn vollständig: andere Antwort setzen
        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage("Danke! Es wurden alle Informationen gesammelt");
            // Start process here
        }
        sessions.put(sessionId, updatedResponse);

        try {
            LOG.info("AI response: {}", updatedResponse.getChatbotMessage());
            return new ChatResponse(sessionId, updatedResponse);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @Retry(maxRetries = 3)
    protected FormData getFormData(String prompt) {
        LOG.info("Prompt to AI: {}", prompt);
        return aiService.chatWithAiStructured(prompt);
    }
}
