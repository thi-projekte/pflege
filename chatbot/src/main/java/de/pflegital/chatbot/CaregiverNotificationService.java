package de.pflegital.chatbot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CaregiverNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(CaregiverNotificationService.class);

    @Inject
    AiService2 aiService2;

    @Inject
    WhatsAppRestClient whatsAppClient;

    /**
     * Sendet eine WhatsApp-Benachrichtigung an eine Pflegekraft über ihre Zuweisung.
     *
     * @param caregiverName Name der Pflegekraft
     * @param caregiverPhoneNumber Telefonnummer der Pflegekraft
     * @param careRecipientName Name des Pflegebedürftigen
     * @param carePeriod Zeitraum der Pflege
     */
    public void notifyCaregiver(String caregiverName, String caregiverPhoneNumber, String careRecipientName, String carePeriod) {
        try {
            // Generiere die Nachricht mit dem AI Service
            String message = aiService2.processRequest(caregiverName);
            
            // Sende die Nachricht über WhatsApp
            whatsAppClient.sendWhatsAppReply(caregiverPhoneNumber, message);
            
            LOG.info("WhatsApp-Benachrichtigung erfolgreich an {} gesendet", caregiverName);
        } catch (Exception e) {
            LOG.error("Fehler beim Senden der WhatsApp-Benachrichtigung an {}: {}", caregiverName, e.getMessage());
            throw new RuntimeException("Fehler beim Senden der WhatsApp-Benachrichtigung", e);
        }
    }
} 