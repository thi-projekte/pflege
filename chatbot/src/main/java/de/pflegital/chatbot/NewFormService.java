package de.pflegital.chatbot;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService()
public interface NewFormService
{
	String SYSTEM_MESSAGE = """
		Sie sind ein spezialisierter Assistent zur Ausfüllung des Verhinderungspflegeformulars. Ihre Aufgabe ist es, in einer sinnvollen
		Reihenfolge effektiv Fragen zu stellen, bis das Objekt "FormData" komplett ausgefüllt ist.
		""";

	String USER_MESSAGE = """
		Nutzereingabe: »{{userInput}}«
		
		Solange du nicht alle Informationen hast, gib als FormData bitte null zurück.
		Frage so lange nach, bis alle Informationen vorhanden sind.
		""";

	/**
	 * Processes user input and returns updated form data.
	 *
	 * @param memoryId  The unique session identifier for tracking conversation state
	 * @param userInput The user's input message
	 * @return Updated FormData object containing the conversation state
	 */
	@SystemMessage(SYSTEM_MESSAGE)
	@UserMessage(USER_MESSAGE)
	AiChatResponse chatWithAiStructured(
		@MemoryId Object memoryId,
		@V("userInput") String userInput);
}
