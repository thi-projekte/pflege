package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

import java.time.LocalDate;

// regular Caregiver
public class Caregiver {

    @JsonProperty("regularCareStartedDate")
    @Description("Date in the past when the caregiver started providing care.")
    private LocalDate regularCareStartedDate;

    @JsonProperty("regularCaregiverName")
    @Description("Full name of the caregiver. This is a required field.")
    private String regularCaregiverName;

    @JsonProperty("regularCaregiverAddress")
    @Description("Address of the caregiver. This is a required field.")
    private Address regularCaregiverAddress;

    @JsonProperty("regularCaregiverPhoneNumber")
    @Description("Phone number of the caregiver. This field is optional but should follow a valid phone number format if provided.")
    private String regularCaregiverPhoneNumber;

    public boolean isRegularCaregiverNameValid() {
        return regularCaregiverName != null && !regularCaregiverName.trim().isEmpty();
    }

    public boolean isRegularCareStartedDateValid() {
        return regularCareStartedDate != null;
    }

    public boolean isRegularCaregiverPhoneNumberValid() {
        return regularCaregiverPhoneNumber == null || regularCaregiverPhoneNumber.matches("^[+\\d][\\d\\s\\-/]{3,}$");
    }

    public boolean isValid() {
        return isRegularCaregiverNameValid()
                && regularCaregiverAddress != null && regularCaregiverAddress.isValid()
                && isRegularCareStartedDateValid()
                && isRegularCaregiverPhoneNumberValid();
    }

    public Caregiver() {
        
    }
    // Getter & Setter

    public LocalDate getRegularCareStartedDate() {
        return regularCareStartedDate;
    }

    public void setRegularCareStartedDate(LocalDate regularCareStartedDate) {
        this.regularCareStartedDate = regularCareStartedDate;
    }

    public Address getRegularCaregiverAddress() {
        return regularCaregiverAddress;
    }

    public void setRegularCaregiverAddress(Address regularCaregiverAddress) {
        this.regularCaregiverAddress = regularCaregiverAddress;
    }

    public String getRegularCaregiverName() {
        return regularCaregiverName;
    }

    public void setRegularCaregiverName(String regularCaregiverName) {
        this.regularCaregiverName = regularCaregiverName;
    }

    public String getRegularCaregiverPhoneNumber() {
        return regularCaregiverPhoneNumber;
    }

    public void setRegularCaregiverPhoneNumber(String regularCaregiverPhoneNumber) {
        this.regularCaregiverPhoneNumber = regularCaregiverPhoneNumber;
    }
}
