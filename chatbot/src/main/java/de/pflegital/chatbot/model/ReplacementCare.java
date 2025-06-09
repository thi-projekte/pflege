package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pflegital.chatbot.model.replacementcare.PrivatePerson;
import de.pflegital.chatbot.model.replacementcare.Provider;
import dev.langchain4j.model.output.structured.Description;

public class ReplacementCare {

    @JsonProperty("isProfessional")
    @Description("True if the care is provided by a professional service provider, otherwise false.")
    private boolean isProfessional;

    @JsonProperty("provider")
    @Description("Details about the professional providing the replacement care. Required if isProfessional is true.")
    private Provider provider;

    @JsonProperty("privatePerson")
    @Description("Details about the private person providing the replacement care. Required if isProfessional is false.")
    private PrivatePerson privatePerson;

    public boolean isValid() {
        if (isProfessional) {
            setPrivatePerson(null);
            return provider != null && provider.isValid();
        } else {
            return privatePerson != null && privatePerson.isValid();
        }
    }

    public boolean isProfessional() {
        return isProfessional;
    }

    public void setProfessional(boolean professional) {
        this.isProfessional = professional;
        if (professional) {
            this.privatePerson = null;
        } else {
            this.provider = null;
        }
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
        this.isProfessional = true;
        this.privatePerson = null;
    }

    public PrivatePerson getPrivatePerson() {
        return privatePerson;
    }

    public void setPrivatePerson(PrivatePerson privatePerson) {
        this.privatePerson = privatePerson;
        this.isProfessional = false;
        this.provider = null;
    }
}
