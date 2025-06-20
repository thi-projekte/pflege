package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import static org.slf4j.LoggerFactory.getLogger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import de.pflegital.chatbot.model.ChatResponse;
import de.pflegital.chatbot.services.AiService;
import de.pflegital.chatbot.services.BpmnProcessService;

@ApplicationScoped
public class Pflegebot {
    @Inject
    AiService aiService;

    @Inject
    SessionStore sessionStore;

    @Inject
    BpmnProcessService bpmnProcessService;

    private static final Logger LOG = getLogger(Pflegebot.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String currentDate = LocalDate.now().format(DATE_FORMATTER);

    public ChatResponse processUserInput(String waId, String userInput) {
        FormData currentFormData = sessionStore.getFormData(waId);
        if (currentFormData == null) {
            currentFormData = new FormData();
            sessionStore.setFormData(waId, currentFormData);
        }

        FormData updatedResponse = aiService.chatWithAiStructured(waId, userInput, currentDate);

        if (updatedResponse.isComplete()) {
            updatedResponse.setChatbotMessage(
                    "✅ Danke! Es wurden alle benötigten Informationen gesammelt! Ich werde Sie über die nächsten Schritte informieren, sobald es möglich ist.");
            bpmnProcessService.startBpmnProcess(updatedResponse, waId);
        }
        sessionStore.setFormData(waId, updatedResponse);

        try {
            return new ChatResponse(waId, updatedResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
