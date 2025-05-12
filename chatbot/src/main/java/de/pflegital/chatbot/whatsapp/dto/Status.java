package de.pflegital.chatbot.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    public String id; // wamid der ursprünglichen Nachricht
    public String status; // sent, delivered, read, failed
    public String timestamp;
    @JsonProperty("recipient_id")
    public String recipientId;
}