package de.pflegital.chatbot;

import de.pflegital.chatbot.api.ProcessWebhookResource;
import de.pflegital.chatbot.exception.WhatsAppApiException;
import de.pflegital.chatbot.model.ChatbotRequest;
import de.pflegital.chatbot.services.ProcessRequestAiService;
import de.pflegital.chatbot.services.WhatsAppRestClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ProcessWebhookResourceTest {
    @Inject
    ProcessWebhookResource resource;
    @InjectMock
    ProcessRequestAiService aiService;
    @InjectMock
    WhatsAppRestClient whatsAppRestClient;

    @Test
    void testCallChatbot_success() {
        ChatbotRequest req = new ChatbotRequest("Testanfrage", "49123456789");
        when(aiService.processRequest("Testanfrage")).thenReturn("Antwort vom Bot");

        String result = resource.callChatbot(req);
        assertEquals("Antwort vom Bot", result);
        verify(aiService).processRequest("Testanfrage");
        verify(whatsAppRestClient).sendWhatsAppReply("49123456789", "Antwort vom Bot");
    }

    @Test
    void testCallChatbot_aiServiceThrowsException() {
        ChatbotRequest req = new ChatbotRequest("Fehler", "49123456789");
        when(aiService.processRequest(anyString())).thenThrow(new RuntimeException("AI kaputt"));

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.callChatbot(req));
        assertTrue(ex.getMessage().contains("Error processing request"));
        verify(aiService).processRequest("Fehler");
        verify(whatsAppRestClient, never()).sendWhatsAppReply(anyString(), anyString());
    }

    @Test
    void testCallChatbot_whatsAppRestClientThrowsException() {
        ChatbotRequest req = new ChatbotRequest("Test", "49123456789");
        when(aiService.processRequest(anyString())).thenReturn("Antwort");
        doThrow(new WhatsAppApiException("WA Fehler")).when(whatsAppRestClient)
                .sendWhatsAppReply(anyString(), anyString());

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.callChatbot(req));
        assertTrue(ex.getCause() instanceof WhatsAppApiException);
        verify(aiService).processRequest("Test");
        verify(whatsAppRestClient).sendWhatsAppReply("49123456789", "Antwort");
    }

    @Test
    void testCallChatbot_nullRequest() {
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> resource.callChatbot(null));
        assertTrue(ex.getMessage().contains("Error processing request"));
    }

    @Test
    void testCallChatbot_emptyRequestFields() {
        ChatbotRequest req = new ChatbotRequest(null, null);
        when(aiService.processRequest(null)).thenReturn("Antwort");
        String result = resource.callChatbot(req);
        assertEquals("Antwort", result);
        verify(aiService).processRequest(null);
        verify(whatsAppRestClient).sendWhatsAppReply(null, "Antwort");
    }
}
