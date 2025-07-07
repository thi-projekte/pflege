package org.acme.travels.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.travels.model.FormData;
import org.acme.travels.model.WaId;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

@ApplicationScoped
public class SendWhatsAppHandler {

    private static final Logger LOG = getLogger(SendWhatsAppHandler.class);
    private static final String WHATSAPP_API_URL_PROD = "https://chatbot-backend.winfprojekt.de/process-webhook";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public void sendToWhatsApp(FormData formData, WaId waId) {

        try {

            String professionalCareGiverName = "Unbekannt";
            if (formData != null && formData.getReplacementCareCareGiver() != null
                    && formData.getReplacementCareCareGiver().getRegularCaregiverName() != null) {
                professionalCareGiverName = formData.getReplacementCareCareGiver().getRegularCaregiverName();
            }
            String jsonPayload = String.format(
                    "{\"request\": \"Bitte weise die Pflegekraft %s hinzu \", \"whatsAppNumber\": \"%s\"}",
                    professionalCareGiverName,
                    waId.getWhatsappId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WHATSAPP_API_URL_PROD))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HTTP_CLIENT
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> LOG.info(
                            "WhatsApp API Antwort, nachdem versucht wird an nummer: {} zu schicken: {}", waId,
                            response.body()))
                    .exceptionally(e -> {
                        LOG.info("Fehler beim Senden an WhatsApp API: {}", e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            LOG.info("Fehler beim Aufruf der WhatsApp API: {}", e.getMessage());
        }
    }
}