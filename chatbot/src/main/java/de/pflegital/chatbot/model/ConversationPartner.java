package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

public enum ConversationPartner {

    @JsonProperty("PFLEGEBEDUERFTIGE_PERSON")
    @Description("Specifies that a care recipient is writing with the chatbot. Use simple, clear, friendly, and caring language.")
    PFLEGEBEDUERFTIGE_PERSON,

    @JsonProperty("ANGEHOERIGE_PERSON")
    @Description("Specifies that a relative of the care recipient is writing with the chatbot. Use a factual, direct, and efficient style.")
    ANGEHOERIGE_PERSON,
}
