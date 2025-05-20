package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class WhatsAppRestClient {

    private static final Logger LOGGER = Logger.getLogger(WhatsAppRestClient.class.getName());

    @ConfigProperty(name = "whatsapp.api.token")
    String whatsappApiToken;

    @ConfigProperty(name = "whatsapp.phone.number.id")
    String whatsappPhoneNumberId;

    @ConfigProperty(name = "whatsapp.api.version")
    String whatsappApiVersion;

    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Startet eine neue Chat-Sitzung über die interne Chat-API.
     */
    public String startSession() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8083/chat/start"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Matcher matcher = Pattern.compile("\"sessionId\":\"([^\"]+)\"").matcher(response.body());
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Fehler beim Starten der Sitzung: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Sendet eine Nachricht an den internen Chatbot und extrahiert die Antwort.
     */
    public String sendToReply(String sessionId, String userInput) {
        try {
            String url = "http://localhost:8083/chat/reply?sessionId=" + sessionId.trim();
            String jsonBody = String.format("{\"userInput\": \"%s\"}", escapeJson(userInput));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Matcher matcher = Pattern.compile("\"chatbotMessage\":\"([^\"]*)\"").matcher(response.body());
            return matcher.find() ? matcher.group(1) : null;

        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Fehler beim Senden an Chatbot: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Sendet eine Antwortnachricht an einen WhatsApp-Benutzer.
     */
    public void sendWhatsAppReply(String recipientWaid, String messageText) {
        try {
            String payload = String.format("""
                    {
                        "messaging_product": "whatsapp",
                        "to": "%s",
                        "type": "text",
                        "text": {
                            "preview_url": false,
                            "body": "%s"
                        }
                    }
                    """, recipientWaid, escapeJson(messageText));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://graph.facebook.com/" + whatsappApiVersion + "/" + whatsappPhoneNumberId
                            + "/messages"))
                    .header("Authorization", "Bearer " + whatsappApiToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                LOGGER.warning("Fehler bei WhatsApp-Antwort: " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Fehler beim Senden an WhatsApp: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Escaped Eingabetext zur sicheren Verwendung in JSON.
     */
    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
