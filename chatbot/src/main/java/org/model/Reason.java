package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

public enum Reason {

    @JsonProperty("URLAUB")
    @Description("Describes the reason for the care. holiday")
    URLAUB,

    @JsonProperty("SONSTIGES")
    @Description("Describes the reason for the care. other.")
    SONSTIGES
}
