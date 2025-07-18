package org.acme.travels.model.replacementcare;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.acme.travels.model.Address;


public class PrivatePerson {

    @JsonProperty("privatePersonName")
   
    private String privatePersonName;

    @JsonProperty("privatePersonAddress")
   
    private Address privatePersonAddress;

    @JsonProperty("privatePersonPhone")
  
    private String privatePersonPhone;

    @JsonProperty("isRelative")

    private Boolean isRelative;

    @JsonProperty("relationDescription")

    private String relationDescription;

    @JsonProperty("isSameHousehold")
   
    private Boolean isSameHousehold;

    @JsonProperty("hasExpenses")
  
    private Boolean hasExpenses;

    @JsonProperty("expenseDescription")
   
    private String expenseDescription;

    public boolean isValid() {
        if (privatePersonName == null || privatePersonName.trim().isEmpty())
            return false;
        if (privatePersonAddress == null || !privatePersonAddress.isValid())
            return false;
        if (isRelative == null)
            return false;
        if (isRelative && (relationDescription == null || relationDescription.trim().isEmpty()))
            return false;
        if (isSameHousehold == null)
            return false;
        if (hasExpenses == null)
            return false;
        if (hasExpenses && (expenseDescription == null || expenseDescription.trim().isEmpty()))
            return false;
        return privatePersonPhone == null || privatePersonPhone.matches("^[+\\d][\\d\\s\\-/]{3,}$");
    }

    public String getPrivatePersonName() {
        return privatePersonName;
    }

    public void setPrivatePersonName(String privatePersonName) {
        this.privatePersonName = privatePersonName;
    }

    public Address getPrivatePersonAddress() {
        return privatePersonAddress;
    }

    public void setPrivatePersonAddress(Address privatePersonAddress) {
        this.privatePersonAddress = privatePersonAddress;
    }

    public String getPrivatePersonPhone() {
        return privatePersonPhone;
    }

    public void setPrivatePersonPhone(String privatePersonPhone) {
        this.privatePersonPhone = privatePersonPhone;
    }

    public Boolean getRelative() {
        return isRelative;
    }

    public void setRelative(Boolean relative) {
        isRelative = relative;
    }

    public String getRelationDescription() {
        return relationDescription;
    }

    public void setRelationDescription(String relationDescription) {
        this.relationDescription = relationDescription;
    }

    public Boolean getSameHousehold() {
        return isSameHousehold;
    }

    public void setSameHousehold(Boolean sameHousehold) {
        isSameHousehold = sameHousehold;
    }

    public Boolean getHasExpenses() {
        return hasExpenses;
    }

    public void setHasExpenses(Boolean hasExpenses) {
        this.hasExpenses = hasExpenses;
    }

    public String getExpenseDescription() {
        return expenseDescription;
    }

    public void setExpenseDescription(String expenseDescription) {
        this.expenseDescription = expenseDescription;
    }
}
