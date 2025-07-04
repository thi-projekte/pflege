package de.pflegital.chatbot.model.replacementcareprovider;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.langchain4j.model.output.structured.Description;

//  Caregiver for Replacementcare, not the one for replacement care. To be filled out automatically by app.pflegital.de
public class ReplacementCareCareGiver {
    @JsonProperty("professionalCareGiverName")
    @Description("Full name of the caregiver for replacement care. This has to be filled not by the chatbot but by app.pflegital.de.")
    private String regularCaregiverName;

    @JsonProperty("replacementCareCaregiverEmail")
    @Description("Email of the caregiver for replacement care. This has to be filled not by the chatbot but by app.pflegital.de.")
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
