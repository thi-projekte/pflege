package de.pflegital.chatbot.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

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
