package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Caregiver {
    @JsonProperty("caregiverEmail")
    private String caregiverEmail;

    @JsonProperty("regularCareStartedDate")
   
    private LocalDate careStartedDate;

    @JsonProperty("regularCaregiverName")
  
    private String caregiverName;

    @JsonProperty("regularCaregiverAddress")
   
    private Address caregiverAddress;

    @JsonProperty("regularCaregiverPhoneNumber")
   
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
    public String getCaregiverEmail() {
    return caregiverEmail;
    }

public void setCaregiverEmail(String caregiverEmail) {
    this.caregiverEmail = caregiverEmail;
    }
        

    public void setCaregiverAddress(Address caregiverAddress) {
        this.caregiverAddress = caregiverAddress;
    }

    @Override
public String toString() {
    return "Caregiver{" +
            "caregiverName='" + caregiverName + '\'' +
            ", caregiverPhoneNumber='" + caregiverPhoneNumber + '\'' +
            ", caregiverAddress=" + caregiverAddress +
            ", careStartedDate='" + careStartedDate + '\'' +
            '}';
}

}
