package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pflegital.chatbot.model.replacementcare.PrivatePerson;
import dev.langchain4j.model.output.structured.Description;

public class ReplacementCare {

    @JsonProperty("isProfessional")
    @Description("True if the care is provided by a professional service provider, otherwise false.")
    private boolean isProfessional;

    private PrivatePerson privatePerson;

    @JsonProperty("privatePerson")
    public PrivatePerson getPrivatePersonForSerialization() {
        return isProfessional ? null : privatePerson;
    }

    public boolean isValid() {
        if (isProfessional) {
            return true; // privatePerson ist ignoriert
        } else {
            return privatePerson != null && privatePerson.isValid();
        }
    }

    public PrivatePerson getPrivatePerson() {
        return privatePerson;
    }

    public void setPrivatePerson(PrivatePerson privatePerson) {
        if (isProfessional && privatePerson != null) {
            throw new IllegalStateException("Cannot set privatePerson when isProfessional is true.");
        }
        this.privatePerson = privatePerson;
    }

    public boolean isProfessional() {
        return isProfessional;
    }

    public void setIsProfessional(boolean isProfessional) {
        this.isProfessional = isProfessional;
        if (isProfessional) {
            this.privatePerson = null;
        }
    }
}
