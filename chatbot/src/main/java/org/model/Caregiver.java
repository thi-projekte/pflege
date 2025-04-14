package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Caregiver {

    @JsonProperty("careStartDate")
    private LocalDate careStartDate;

    @JsonProperty("caregiverName")
    private String caregiverName;

    @JsonProperty("caregiverAddress")
    private Address caregiverAddress;

    @JsonProperty("caregiverPhoneNumber")
    private String caregiverPhoneNumber;

    // Getter & Setter

    public LocalDate getCareStartDate() {
        return careStartDate;
    }

    public void setCareStartDate(LocalDate careStartDate) {
        this.careStartDate = careStartDate;
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
