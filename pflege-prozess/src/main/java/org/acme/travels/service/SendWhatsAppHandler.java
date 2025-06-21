package org.acme.travels.service;
 
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.travels.model.FormData;
import org.acme.travels.model.WaId;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
 
@ApplicationScoped
public class SendWhatsAppHandler {
 
    public final String WHATSAPP_API_URL = "http://localhost:8084/process-webhook";
 
    public void sendToWhatsApp(FormData formData, WaId waId) {
        
        try {
            
            String caregiverName = "Unbekannt";
            if (formData != null && formData.getCaregiver() != null && formData.getCaregiver().getCaregiverName() != null) {
                caregiverName = formData.getCaregiver().getCaregiverName();
            }
            String jsonPayload = String.format(
                "{\"request\": \"Bitte weise die Pflegekraft %s hinzu \", \"whatsAppNumber\": \"%s\"}",
                caregiverName,
                waId.getWaId()
            );
 

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WHATSAPP_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
 
            HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("WhatsApp API Antwort, nachdem versucht wird an nummer: " + waId + "zu schicken:" + response.body());
                    })
                    .exceptionally(e -> {
                        System.err.println("Fehler beim Senden an WhatsApp API: " + e.getMessage());
                        return null;
                    });
 
        } catch (Exception e) {
            System.err.println("Fehler beim Aufruf der WhatsApp API: " + e.getMessage());
        }
    }
}