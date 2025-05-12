package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

import java.time.LocalDate;

public class Carerecipient {

    @JsonProperty("fullName")
    @Description("Full name of the insured person (first name and surname). This is a required field and must not be empty.")
    private String fullName;

    @JsonProperty("birthDate")
    @Description("Date of birth of the insured person. Must be a date in the past.")
    private LocalDate birthDate;

    @JsonProperty("insuredAddress")
    @Description("Address of the insured person including street, house number, postal code and city.")
    private Address insuredAddress;

    @JsonProperty("phoneNumber")
    @Description("Phone number of the insured person. This field is optional but should follow a valid phone number format.")
    private String phoneNumber;

    @JsonProperty("insuranceNumber")
    @Description("Insurance number of the insured person. Must contain 10 to 12 digits and is a required field.")
    private String insuranceNumber;

    public boolean isInsuranceNumberValid() {
        return insuranceNumber != null && insuranceNumber.trim().matches("^\\d{10,12}$");
    }

    public boolean isFullNameValid() {
        return fullName != null && !fullName.trim().isEmpty();
    }

    public boolean isBirthDateValid() {
        return birthDate != null && birthDate.isBefore(LocalDate.now());
    }

    public boolean isPhoneNumberValid() {
        return phoneNumber == null || phoneNumber.matches("^[+\\d][\\d\\s\\-/]{3,}$");
    }

    public boolean isValid() {
        return isInsuranceNumberValid() &&
                isFullNameValid() &&
                isBirthDateValid() &&
                insuredAddress != null && insuredAddress.isValid();
    }

    // Getter & Setter

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public Address getInsuredAddress() {
        return insuredAddress;
    }

    public void setInsuredAddress(Address insuredAddress) {
        this.insuredAddress = insuredAddress;
    }
}
