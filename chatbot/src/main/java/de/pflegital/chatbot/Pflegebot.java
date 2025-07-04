package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import de.pflegital.chatbot.exception.ChatResponseCreationException;
import de.pflegital.chatbot.model.ChatResponse;
import de.pflegital.chatbot.services.AiService;
import de.pflegital.chatbot.services.BpmnProcessService;
import de.pflegital.chatbot.tools.FormDataCompleted;

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

    @Inject
    FormDataCompleted formDataCompleted;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String currentDate = LocalDate.now().format(DATE_FORMATTER);

    public ChatResponse processUserInput(String waId, String userInput) {
        if (userInput != null && userInput.trim().equalsIgnoreCase("reset")) {
            // Vorherige Daten holen
            FormData oldFormData = sessionStore.getFormData(waId);
            int formDataCount = oldFormData != null ? 1 : 0;
            int chatMsgCount = chatMemoryStore.getMessages(waId).size();
            // Löschen
            sessionStore.removeFormData(waId);
            chatMemoryStore.deleteMessages(waId);
            // Rückmeldung
            FormData resetFormData = new FormData();
            resetFormData.setChatbotMessage(
                    "🧹 Der Testbot wurde zurückgesetzt. Es wurden " + formDataCount + " Formulardatensatz und "
                            + chatMsgCount + " Chatnachrichten gelöscht. Du kannst jetzt von vorne beginnen.");
            return new ChatResponse(waId, resetFormData);
        }
        FormData currentFormData = sessionStore.getFormData(waId);
        if (currentFormData == null) {
            currentFormData = new FormData();
            sessionStore.setFormData(waId, currentFormData);
        }

        FormData updatedResponse = aiService.chatWithAiStructured(waId, userInput, currentDate);

        String validation = formDataCompleted.checkFormData(updatedResponse);
        if ("Alle erforderlichen Felder sind ausgefüllt und gültig!".equals(validation)) {
            updatedResponse.setChatbotMessage(
                    "\u2705 Danke! Es wurden alle benötigten Informationen gesammelt! Ich werde Sie über die nächsten Schritte informieren, sobald es möglich ist.");
            bpmnProcessService.startBpmnProcess(updatedResponse, waId);
        }
        sessionStore.setFormData(waId, updatedResponse);

        try {
            return new ChatResponse(waId, updatedResponse);
        } catch (Exception e) {
            throw new ChatResponseCreationException("Fehler beim Erstellen der ChatResponse für waId: " + waId, e);
        }
    }
}
