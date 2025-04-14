package org.model.replacementCare;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.model.Address;

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
