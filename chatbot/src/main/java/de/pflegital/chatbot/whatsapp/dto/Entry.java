package de.pflegital.chatbot.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Entry {
    public String id;
    public List<Change> changes;
}