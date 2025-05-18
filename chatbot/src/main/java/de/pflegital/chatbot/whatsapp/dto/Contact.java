package de.pflegital.chatbot.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {
    public Profile profile;
    @JsonProperty("wa_id")
    public String waId;
}
