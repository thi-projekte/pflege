package org.acme.travels.model.replacementcare;

import com.fasterxml.jackson.annotation.JsonProperty;


//  Caregiver for Replacementcare, not the one for replacement care. To be filled out automatically by app.pflegital.de
public class ReplacementCareCareGiver {
    @JsonProperty("replacementCareCaregiver")
    private String regularCaregiverName;


    @JsonProperty("replacementCareCaregiverEmail")
    private String email;

    public String getRegularCaregiverName() {
        return regularCaregiverName;
    }

    public void setRegularCaregiverName(String regularCaregiverName) {
        this.regularCaregiverName = regularCaregiverName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
