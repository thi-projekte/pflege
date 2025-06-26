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

    @Inject
    ChatMemoryStore chatMemoryStore;

    private static final Logger LOG = getLogger(Pflegebot.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String currentDate = LocalDate.now().format(DATE_FORMATTER);

    public ChatResponse processUserInput(String waId, String userInput) {
        if (userInput != null && userInput.trim().equalsIgnoreCase("reset")) {
            // Vorherige Daten holen
            FormData oldFormData = sessionStore.getFormData(waId);
            int formDataCount = oldFormData != null ? 1 : 0;
            int chatMsgCount = chatMemoryStore.getMessages(waId).size();
            LOG.info("[RESET] Vor dem Löschen: {} Nachrichten im ChatMemory für waId {}", chatMsgCount, waId);
            // Löschen
            sessionStore.removeFormData(waId);
            chatMemoryStore.deleteMessages(waId);
            int chatMsgCountAfter = chatMemoryStore.getMessages(waId).size();
            LOG.info("[RESET] Nach dem Löschen: {} Nachrichten im ChatMemory für waId {}", chatMsgCountAfter, waId);
            // Rückmeldung
            FormData resetFormData = new FormData();
            resetFormData.setChatbotMessage(
                    "🧹 Der Testbot wurde zurückgesetzt. Es wurden " + formDataCount + " Formulardatensatz und "
                            + chatMsgCount + " Chatnachrichten gelöscht. Du kannst jetzt von vorne beginnen.");
            return new ChatResponse(waId, resetFormData);
        }
        FormData currentFormData = sessionStore.getFormData(waId);
        int chatMsgCountCurrent = chatMemoryStore.getMessages(waId).size();
        LOG.info("[PROCESS] Aktuell {} Nachrichten im ChatMemory für waId {}", chatMsgCountCurrent, waId);
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
