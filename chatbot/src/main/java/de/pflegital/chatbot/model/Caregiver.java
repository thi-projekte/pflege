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
    private String caregiverName;

    @JsonProperty("regularCaregiverAddress")
    @Description("Address of the caregiver. This is a required field.")
    private Address caregiverAddress;

    @JsonProperty("regularCaregiverPhoneNumber")
    @Description("Phone number of the caregiver. This field is optional but should follow a valid phone number format if provided.")
    private String caregiverPhoneNumber;

    public boolean isCaregiverNameValid() {
        return caregiverName != null && !caregiverName.trim().isEmpty();
    }

    public boolean isRegularCareStartedDateValid() {
        return regularCareStartedDate != null && regularCareStartedDate.isBefore(LocalDate.now()) && regularCareStartedDate.isAfter(LocalDate.now().minusMonths(6));
    }

    public boolean isCaregiverPhoneNumberValid() {
        return caregiverPhoneNumber == null || caregiverPhoneNumber.matches("^[+\\d][\\d\\s\\-/]{3,}$");
    }

    public boolean isValid() {
        return isCaregiverNameValid()
                && caregiverAddress != null && caregiverAddress.isValid()
                && isRegularCareStartedDateValid()
                && isCaregiverPhoneNumberValid();
    }

    // Getter & Setter

    public LocalDate getRegularCareStartedDate() {
        return regularCareStartedDate;
    }

    public void setRegularCareStartedDate(LocalDate regularCareStartedDate) {
        this.regularCareStartedDate = regularCareStartedDate;
    }

    public String getCaregiverName() {
        return caregiverName;
    }

    public void setCaregiverName(String caregiverName) {
        this.caregiverName = caregiverName;
    }

    public String getCaregiverPhoneNumber() {
        return caregiverPhoneNumber;
    }

    public void setCaregiverPhoneNumber(String caregiverPhoneNumber) {
        this.caregiverPhoneNumber = caregiverPhoneNumber;
    }

    public Address getCaregiverAddress() {
        return caregiverAddress;
    }

    public void setCaregiverAddress(Address caregiverAddress) {
        this.caregiverAddress = caregiverAddress;
    }
}
