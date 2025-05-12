package de.pflegital.chatbot.whatsapp.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Wichtig für Robustheit
public class WebhookPayload {
    public String object;       // Enthält z.B. "whatsapp_business_account"
    public List<Entry> entry; // Enthält eine Liste der eigentlichen Ereignisse
}