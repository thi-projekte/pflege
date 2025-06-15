package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.LocalDate;

public class Carerecipient {

    @JsonProperty("fullName")
 
    private String fullName;

    @JsonProperty("birthDate")
  
    private LocalDate birthDate;

    @JsonProperty("insuredAddress")
   
    private Address insuredAddress;

    @JsonProperty("phoneNumber")
  
    private String phoneNumber;

    @JsonProperty("insuranceNumber")
   
    private String insuranceNumber;

    public boolean isInsuranceNumberValid() {
        return insuranceNumber != null &&
                !insuranceNumber.trim().isEmpty() &&
                insuranceNumber.trim().length() >= 10 &&
                insuranceNumber.trim().length() <= 12;
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
