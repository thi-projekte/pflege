package de.pflegital.chatbot.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    public String from; // Absender (Nutzer)
    public String id;   // Nachrichten-ID (wamid)
    public String timestamp;
    public String type; // text, image, etc.
    public Text text;
    // Hier weitere Typen bei Bedarf hinzufügen (Button, Interactive, Image, etc.)
    // public Button button;
    // public Interactive interactive;
    // public Image image;
}