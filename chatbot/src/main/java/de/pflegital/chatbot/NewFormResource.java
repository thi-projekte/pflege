package de.pflegital.chatbot;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Path("/newchat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NewFormResource
{
	private static final Logger LOG = getLogger(NewFormResource.class);

	@Inject
	NewFormService aiService;

	@Inject
	ChatMemoryStore chatMemoryStore;

	@POST
	@Path("/start")
	public AiChatResponse startChat()
	{
		String memoryId = UUID.randomUUID().toString();

		AiChatResponse aiChatResponse = aiService.chatWithAiStructured(memoryId, "Start conversation.");
		LOG.info("Chat started: {}", aiChatResponse.getChatBotResponse());
		aiChatResponse.setMemoryId(memoryId);
		return aiChatResponse;
	}

	@POST
	@Path("/reply")
	public AiChatResponse processUserInput(@QueryParam("memoryId") String memoryId, String userInput)
	{
		List<ChatMessage> memory = chatMemoryStore.getMessages(memoryId);
		LOG.info("Memory has {} items", memory.size());
		return aiService.chatWithAiStructured(memoryId, userInput);
	}
}
