package de.pflegital.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.pflegital.chatbot.whatsapp.WhatsAppService; // Dein Sende-Service
import de.pflegital.chatbot.whatsapp.dto.WebhookPayload; // Deine DTOs
import io.quarkus.logging.Log; // Einfaches Quarkus Logging
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;


@Path("/webhook") // Der Pfad für den WhatsApp Webhook
@ApplicationScoped
public class WhatsAppWebhookResource {

    private static final Logger LOG = getLogger(WhatsAppWebhookResource.class);

    // --- Injizierte Abhängigkeiten ---
    @Inject
    AiService aiService; // Dein Service für die AI-Logik

    @Inject
    FormDataPresenter formDataPresenter; // Dein Service zum Formatieren von FormData

    @Inject
    InsuranceNumberTool insuranceNumberTool; // Dein Tool zur Validierung

    @Inject
    WhatsAppService whatsAppService; // Dein Service zum Senden von WhatsApp Nachrichten

    @Inject
    ObjectMapper objectMapper; // Jackson ObjectMapper zum Parsen

    // --- Konfigurationswerte ---
    @ConfigProperty(name = "pflegital.whatsapp.verify-token")
    String verifyToken;

    @ConfigProperty(name = "pflegital.whatsapp.app-secret")
    String appSecret;

    // --- Session Management ---
    // Verwende WhatsApp ID (senderId) als Key
    private final Map<String, FormData> sessions = new ConcurrentHashMap<>();

    // === GET Request für Webhook Verifizierung ===
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response verifyWebhook(
            @QueryParam("hub.mode") String mode,
            @QueryParam("hub.challenge") String challenge,
            @QueryParam("hub.verify_token") String token) {

        Log.infof("GET /webhook zur Verifizierung erhalten. Mode: %s, Token: %s", mode, token != null ? "[PRESENT]" : "[MISSING]");

        if (mode != null && token != null && mode.equals("subscribe") && token.equals(verifyToken)) {
            Log.info("Webhook verifiziert. Sende Challenge zurück.");
            return Response.ok(challenge).build();
        } else {
            Log.warn("Webhook Verifizierung fehlgeschlagen.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    // === POST Request für Nachrichtenempfang ===
    // @Blocking: Wichtig, da AI-Aufrufe und das Senden von Nachrichten blockieren können
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN) // Wir geben nur Text/Status an Meta zurück
    @Blocking
    public Response handleWebhook(
            String rawPayload, // Roher Body für Signaturprüfung
            @HeaderParam("X-Hub-Signature-256") String signatureHeader) {

        Log.debug("POST /webhook empfangen.");
        // Logge den Payload nur bei Bedarf und mit Vorsicht bzgl. sensibler Daten
        // Log.trace("Raw Payload: {}", rawPayload);

        // 1. Signatur prüfen (ESSENTIELL!)
        if (!isValidSignature(rawPayload, signatureHeader)) {
            Log.error("Ungültige Signatur empfangen! Anfrage wird abgelehnt.");
            // Gib 403 zurück, damit Meta das Problem erkennt
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid Signature").build();
        }
        Log.info("Signatur erfolgreich verifiziert.");

        try {
            // 2. Payload parsen
            WebhookPayload payload = parsePayload(rawPayload);

            if (payload != null && "whatsapp_business_account".equals(payload.object)) {
                for (Entry entry : payload.entry) {
                    if (entry.changes == null) continue;
                    for (Change change : entry.changes) {
                        if ("messages".equals(change.field) && change.value != null) {

                            // Eingehende Nachrichten verarbeiten
                            if (change.value.messages != null) {
                                for (Message message : change.value.messages) {
                                    // Aktuell nur Textnachrichten behandeln
                                    if ("text".equals(message.type) && message.text != null && message.from != null) {
                                        String senderId = message.from;
                                        String userMessage = message.text.body;
                                        Log.infof("Nachricht von %s empfangen: '%s'", senderId, userMessage);

                                        // Asynchrone Verarbeitung wäre hier ideal,
                                        // aber @Blocking reicht für den Anfang
                                        processMessageWithChatbot(senderId, userMessage);

                                    } else {
                                        Log.infof("Nachricht von %s erhalten (Typ: %s), wird ignoriert.", message.from, message.type);
                                        // Optional: Standardantwort senden für nicht unterstützte Typen
                                        // if (message.from != null) {
                                        //    whatsAppService.sendTextMessage(message.from, "Ich kann momentan nur Textnachrichten verarbeiten.");
                                        // }
                                    }
                                }
                            }

                            // Status-Updates loggen (optional)
                            if (change.value.statuses != null) {
                                for (Status status : change.value.statuses) {
                                     Log.debugf("Status Update für Nachricht %s an %s: %s", status.id, status.recipientId, status.status);
                                     // Hier könnte Logik für Zustellberichte implementiert werden
                                 }
                             }
                        } // endif "messages" field
                    } // end changes loop
                } // end entry loop

                // 3. Erfolgreich verarbeitet (oder zumindest angenommen), 200 OK an Meta senden
                return Response.ok("EVENT_RECEIVED").build();

            } else {
                Log.warn("Empfangenes Objekt ist nicht 'whatsapp_business_account' oder Payload ist null.");
                // Sende trotzdem 200, damit Meta nicht wiederholt
                 return Response.ok("UNKNOWN_PAYLOAD_TYPE").build();
            }

        } catch (JsonProcessingException jsonEx) {
             Log.error("Fehler beim Parsen des Webhook JSON Payloads: {}", jsonEx.getMessage());
             // Sende trotzdem 200, Meta kann damit nichts anfangen
              return Response.ok("JSON_PARSE_ERROR").build();
        }
        catch (Exception e) {
            Log.error("Unerwarteter Fehler bei der Verarbeitung des Webhook Payloads: {}", e.getMessage(), e);
            // Wichtig: Auch bei internen Fehlern 200 OK senden, sonst sendet Meta immer wieder.
            // Fehler müssen intern geloggt und behoben werden.
            return Response.ok("INTERNAL_PROCESSING_ERROR").build();
        }
    }

    // --- Hilfsmethode zur Signaturprüfung ---
    private boolean isValidSignature(String payload, String signatureHeader) {
        if (appSecret == null || appSecret.isBlank()) {
            Log.error("APP_SECRET ist nicht konfiguriert! Signaturprüfung kann nicht durchgeführt werden.");
            // In einer produktiven Umgebung sollte dies ein harter Fehler sein.
            // Für lokale Tests KÖNNTE man es temporär erlauben (NICHT EMPFOHLEN!)
            // return true; // ACHTUNG: NUR FÜR LOKALE TESTS OHNE NGINX/HTTPS!
             return false; // Sicherer Standard
        }
        if (payload == null || payload.isEmpty()) {
            Log.warn("Signaturprüfung: Payload ist leer.");
            return false; // Keine Signatur für leeren Payload möglich/sinnvoll
        }
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            Log.warn("Signaturprüfung: Header fehlt oder ist ungültig formatiert.");
            return false;
        }

        String expectedSignature = signatureHeader.substring(7); // Entferne "sha256="

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // Konvertiere Byte-Array zu Hex-String
            String calculatedSignature = bytesToHex(hash);

            // Sicherer Vergleich (verhindert Timing-Attacken)
            boolean signaturesMatch = MessageDigest.isEqual(
                    calculatedSignature.getBytes(StandardCharsets.UTF_8),
                    expectedSignature.getBytes(StandardCharsets.UTF_8)
            );

            if (!signaturesMatch) {
               Log.warn("Signatur stimmt nicht überein! Erwartet: {}, Bekommen: {}", calculatedSignature, expectedSignature);
            }
            return signaturesMatch;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Log.error("Fehler bei der HMAC SHA256 Berechnung: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            Log.error("Unerwarteter Fehler bei der Signaturprüfung: {}", e.getMessage(), e);
            return false;
        }
    }

     // Hilfsmethode zur Hex-Konvertierung
     private static String bytesToHex(byte[] bytes) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }


    // --- Hilfsmethode zum Parsen des Payloads ---
     private WebhookPayload parsePayload(String rawPayload) throws JsonProcessingException {
         // Konfiguriere ObjectMapper bei Bedarf (z.B. unknown properties ignorieren)
         // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         return objectMapper.readValue(rawPayload, WebhookPayload.class);
     }

    // --- Kernlogik: Nachricht verarbeiten und an Chatbot senden ---
    private void processMessageWithChatbot(String senderId, String userInput) {
        // Session holen oder neu erstellen
        FormData session = sessions.computeIfAbsent(senderId, k -> {
            Log.infof("Neue Session für WhatsApp Nutzer %s gestartet.", k);
            // Ggf. initiales FormData-Objekt erstellen oder laden
            return new FormData(); // Beispiel: Leeres Objekt
        });

        Log.infof("Verarbeite Nachricht für Nutzer %s: '%s'", senderId, userInput);

        // Prompt bauen
        String jsonFormData = formDataPresenter.present(session);
        String prompt = "The current form data is: " + jsonFormData +
                ". The user (via WhatsApp) just said: '" + userInput +
                "'. Please update the missing fields accordingly and formulate a concise response for WhatsApp.";
        Log.infof("Prompt an AI für Nutzer %s: %s", senderId, prompt);

        try {
            // AI aufrufen
            FormData updatedResponse = aiService.chatWithAiStructured(prompt);

            // Business-Regeln anwenden
            applyBusinessRules(updatedResponse);

            // Aktualisierte Session speichern
            sessions.put(senderId, updatedResponse);

            // Antwort vom Chatbot holen
            String aiResponseMessage = updatedResponse.getChatbotMessage();
            if (aiResponseMessage == null || aiResponseMessage.isBlank()) {
                aiResponseMessage = "Ich habe dazu leider keine passende Antwort. Bitte versuchen Sie es anders.";
                Log.warn("AI hat keine Antwortnachricht geliefert für Nutzer {}.", senderId);
            }

            Log.infof("Sende AI Antwort an %s: '%s'", senderId, aiResponseMessage);

            // Antwort über WhatsAppService zurücksenden
            whatsAppService.sendTextMessage(senderId, aiResponseMessage);

             // Optional: Wenn Formular komplett, Session entfernen oder markieren
             if (updatedResponse.isComplete()) {
                 Log.infof("Formular für Nutzer %s ist vollständig.", senderId);
                 // sessions.remove(senderId); // Session entfernen? Oder für Follow-up behalten?
             }
        } catch (Exception e) {
             Log.error("Fehler bei der AI-Verarbeitung oder beim Senden der Antwort für Nutzer {}: {}", senderId, e.getMessage(), e);
             // Optional: Eine Fehlermeldung an den Nutzer senden
              try {
                  whatsAppService.sendTextMessage(senderId, "Es ist ein interner Fehler aufgetreten. Bitte versuchen Sie es später erneut.");
              } catch (Exception sendEx) {
                  Log.error("Konnte nicht einmal die Fehlermeldung an Nutzer {} senden: {}", senderId, sendEx.getMessage());
              }
        }
    }

     // --- Hilfsmethode für Business-Regeln ---
     private void applyBusinessRules(FormData responseData) {
        if (responseData == null) return; // Sicherstellen, dass responseData nicht null ist

        // Prüfe Pflegegrad nur, wenn vorhanden
        if (responseData.getCareLevel() != null && responseData.getCareLevel() < 2) {
              Log.debug("Business Rule: Pflegegrad < 2 erkannt.");
              responseData.setChatbotMessage(
                      "Die Verhinderungspflege steht erst ab Pflegegrad 2 zur Verfügung. Bitte prüfen Sie Ihre Angaben.");
              return; // Regel hat gegriffen, keine weiteren Prüfungen für die Nachricht nötig
        }

        // Prüfe Versicherungsnummer nur, wenn vorhanden
        if (responseData.getCareRecipient() != null &&
                responseData.getCareRecipient().getInsuranceNumber() != null &&
                !insuranceNumberTool.isValidSecurityNumber(responseData.getCareRecipient().getInsuranceNumber())) {
             Log.debug("Business Rule: Ungültige Versicherungsnummer erkannt.");
             responseData.setChatbotMessage(
                     "Die angegebene Versicherungsnummer scheint ungültig zu sein. Bitte überprüfen Sie Ihre Eingabe.");
             return; // Regel hat gegriffen
        }

        // Setze Abschlussnachricht nur, wenn das Formular komplett ist UND noch keine Nachricht gesetzt wurde
        if (responseData.isComplete() && (responseData.getChatbotMessage() == null || responseData.getChatbotMessage().isEmpty())) {
             Log.debug("Business Rule: Formular ist vollständig.");
             responseData.setChatbotMessage("Vielen Dank! Alle benötigten Informationen wurden erfasst.");
              // FIXME: Hier den Folgeprozess starten (z.B. Daten speichern, E-Mail senden)
        }
     }
}