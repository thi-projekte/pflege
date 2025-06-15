package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.acme.travels.model.replacementcare.PrivatePerson;
import org.acme.travels.model.replacementcare.Provider;


public class ReplacementCare {

    @JsonProperty("isProfessional")
  
    private boolean isProfessional;

    @JsonProperty("provider")
 
    private Provider provider;

    @JsonProperty("privatePerson")
   
    private PrivatePerson privatePerson;

    public boolean isValid() {
        if (isProfessional)
            return false;
        if (isProfessional) {
            return provider != null && provider.isValid();
        } else {
            return privatePerson != null && privatePerson.isValid();
        }

    }

    public boolean isProfessional() {
        return isProfessional;
    }

    public void setProfessional(boolean professional) {
        isProfessional = professional;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public PrivatePerson getPrivatePerson() {
        return privatePerson;
    }

    public void setPrivatePerson(PrivatePerson privatePerson) {
        this.privatePerson = privatePerson;
    }
}
