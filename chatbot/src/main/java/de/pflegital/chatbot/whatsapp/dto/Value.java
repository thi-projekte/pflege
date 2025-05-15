package de.pflegital.chatbot.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
public class Value {
    public String messaging_product;
    public Metadata metadata;
    public List<Contact> contacts;
    public List<Message> messages;
    public List<Status> statuses; // Kann null sein, wenn keine Status-Updates
}