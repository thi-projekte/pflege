package de.pflegital.chatbot.model.replacementcare;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pflegital.chatbot.model.Address;
import dev.langchain4j.model.output.structured.Description;

public class PrivatePerson {

    @JsonProperty("privatePersonName")
    @Description("Full name of the private caregiver. Required field.")
    private String privatePersonName;

    @JsonProperty("privatePersonAddress")
    @Description("Address of the private caregiver. Required field.")
    private Address privatePersonAddress;

    @JsonProperty("privatePersonPhone")
    @Description("Phone number of the private caregiver. This field is optional but should follow a valid phone number format.")
    private String privatePersonPhone;

    @JsonProperty("isRelative")
    @Description("Indicates whether the private caregiver is related to the insured person. Required field.")
    private Boolean isRelative;

    @JsonProperty("relationDescription")
    @Description("Description of the relation to the insured person. Required if isRelative is true.")
    private String relationDescription;

    @JsonProperty("isSameHousehold")
    @Description("Indicates whether the caregiver lives in the same household as the insured person. Required field.")
    private Boolean isSameHousehold;

    @JsonProperty("hasExpenses")
    @Description("Indicates whether the caregiver had any expenses. Required field.")
    private Boolean hasExpenses;

    @JsonProperty("expenseDescription")
    @Description("Description of the expenses incurred. Required if hasExpenses is true.")
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
