package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public enum ConversationPartner {

    @JsonProperty("PFLEGEBEDUERFTIGE_PERSON")
  
    PFLEGEBEDUERFTIGE_PERSON,

    @JsonProperty("ANGEHOERIGE_PERSON")
   
    ANGEHOERIGE_PERSON,
}
