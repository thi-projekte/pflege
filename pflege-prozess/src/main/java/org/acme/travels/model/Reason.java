package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public enum Reason {

    @JsonProperty("URLAUB")
   
    URLAUB,

    @JsonProperty("SONSTIGES")
   
    SONSTIGES
}
