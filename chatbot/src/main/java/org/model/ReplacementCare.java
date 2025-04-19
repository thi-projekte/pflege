package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.model.replacementCare.PrivatePerson;
import org.model.replacementCare.Provider;
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

    public boolean isProfessionalValid(){ return isProfessional ;}

    public boolean isValid(){
        if (isProfessionalValid() == false) return false;
        if(isProfessional == true) {
            return provider != null && provider.isValid();
        }
        else{
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
