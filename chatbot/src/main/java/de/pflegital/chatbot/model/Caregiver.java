package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

import java.time.LocalDate;

public class Caregiver {

    @JsonProperty("careStartedDate")
    @Description("Date in the past when the caregiver started providing care.")
    private LocalDate careStartedDate;

    @JsonProperty("caregiverName")
    @Description("Full name of the caregiver. This is a required field.")
    private String caregiverName;

    @JsonProperty("caregiverAddress")
    @Description("Address of the caregiver. This is a required field.")
    private Address caregiverAddress;

    @JsonProperty("caregiverPhoneNumber")
    @Description("Phone number of the caregiver. This field is optional but should follow a valid phone number format if provided.")
    private String caregiverPhoneNumber;

    public boolean isCaregiverNameValid() {
        return caregiverName != null && !caregiverName.trim().isEmpty();
    }

    public boolean isCareStartedDateValid() {
        return careStartedDate != null;
    }

    public boolean isCaregiverPhoneNumberValid() {
        return caregiverPhoneNumber == null || caregiverPhoneNumber.matches("^[+\\d][\\d\\s\\-/]{3,}$");
    }

    public boolean isValid() {
        return isCaregiverNameValid()
                && caregiverAddress != null && caregiverAddress.isValid()
                && isCareStartedDateValid()
                && isCaregiverPhoneNumberValid();
    }

    // Getter & Setter

    public LocalDate getCareStartDate() {
        return careStartedDate;
    }

    public void setCareStartDate(LocalDate careStartedDate) {
        this.careStartedDate = careStartedDate;
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
