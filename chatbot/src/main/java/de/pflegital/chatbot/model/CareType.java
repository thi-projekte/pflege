package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

public enum CareType {

    @JsonProperty("STUNDENWEISE")
    @Description("Describes the type of care. hourly")
    STUNDENWEISE,

    @JsonProperty("TAGEWEISE")
    @Description("Describes the type of care. daily.")
    TAGEWEISE
}
