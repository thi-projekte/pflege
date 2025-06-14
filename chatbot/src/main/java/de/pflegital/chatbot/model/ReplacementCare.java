package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pflegital.chatbot.model.replacementcare.PrivatePerson;
import dev.langchain4j.model.output.structured.Description;

public class ReplacementCare {

    @JsonProperty("isProfessional")
    @Description("True if the care is provided by a professional service provider, otherwise false.")
    private boolean isProfessional;


    @JsonProperty("privatePerson")
    @Description("Details about the private person providing the replacement care. Only required if isProfessional is false.")
    private PrivatePerson privatePerson;

    public boolean isValid() {
        if (isProfessional) {
            setPrivatePerson(null);
            return true;
        } else {
            return privatePerson != null && privatePerson.isValid();
        }
    }

    public PrivatePerson getPrivatePerson() {
        return privatePerson;
    }

    public void setPrivatePerson(PrivatePerson privatePerson) {
        this.privatePerson = privatePerson;
        this.isProfessional = false;
    }

    public boolean isProfessional() {
        return isProfessional;
    }
    public void setIsProfessional(boolean isProfessional) {
        this.isProfessional = isProfessional;
    }
}
