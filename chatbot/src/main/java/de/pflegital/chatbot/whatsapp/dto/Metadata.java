package de.pflegital.chatbot.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    @JsonProperty("display_phone_number")
    public String displayPhoneNumber;
    @JsonProperty("phone_number_id")
    public String phoneNumberId;
}
