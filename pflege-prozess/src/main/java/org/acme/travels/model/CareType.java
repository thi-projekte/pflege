package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CareType {

    @JsonProperty("STUNDENWEISE")
 
    STUNDENWEISE,

    @JsonProperty("TAGEWEISE")
   
    TAGEWEISE
}
