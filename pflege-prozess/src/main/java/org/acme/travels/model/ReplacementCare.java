package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.acme.travels.model.replacementcare.PrivatePerson;


public class ReplacementCare {

    @JsonProperty("isProfessional")
  
    private boolean isProfessional;


    @JsonProperty("privatePerson")
   
    private PrivatePerson privatePerson;



    public boolean isProfessional() {
        return isProfessional;
    }

    public void setProfessional(boolean professional) {
        isProfessional = professional;
    }


    public PrivatePerson getPrivatePerson() {
        return privatePerson;
    }

    public void setPrivatePerson(PrivatePerson privatePerson) {
        this.privatePerson = privatePerson;
    }
}
