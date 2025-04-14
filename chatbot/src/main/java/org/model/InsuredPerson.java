package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Date;

public class InsuredPerson {

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
