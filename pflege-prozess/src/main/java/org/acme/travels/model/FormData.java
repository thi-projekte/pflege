package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.acme.travels.model.*;
import org.acme.travels.model.replacementcare.ReplacementCareCareGiver;


public class FormData {



    @JsonProperty("chatbotMessage")
    private String chatbotMessage;

    @JsonProperty("conversationPartner")
  
    private ConversationPartner conversationPartner;

    @JsonProperty("careType")
   
    private CareType careType;

    @JsonProperty("careLevel")
  
    private Integer careLevel;

    @JsonProperty("carePeriod")
  
    private Period carePeriod;

    @JsonProperty("reason")

    private Reason reason;

    @JsonProperty("careRecipient")

    private Carerecipient careRecipient;

    @JsonProperty("caregiver")
   
    private Caregiver caregiver;

    @JsonProperty("replacementCare")
  
    private ReplacementCare replacementCare;

    @JsonProperty("isHomeCare")
   
    private Boolean isHomeCare;

    @JsonProperty("careDurationMin6Months")
   
    private Boolean careDurationMin6Months;

    @JsonProperty("legalAcknowledgement")
  
    private Boolean legalAcknowledgement;

    @JsonProperty("professionalCareGiverName")
    private ReplacementCareCareGiver replacementCareCareGiver;

    // Getter & Setter

    public ReplacementCareCareGiver  getReplacementCareCareGiver() {
        return replacementCareCareGiver;
    }

    public void setReplacementCareCareGiver(ReplacementCareCareGiver replacementCareCareGiver) {
        this.replacementCareCareGiver = replacementCareCareGiver;
    }

    public CareType getCareType() {
        return careType;
    }

    public void setCareType(CareType careType) {
        this.careType = careType;
    }

    public String getChatbotMessage() {
        return chatbotMessage;
    }

    public void setChatbotMessage(String chatbotMessage) {
        this.chatbotMessage = chatbotMessage;
    }

    public Integer getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(Integer careLevel) {
        this.careLevel = careLevel;
    }

    public Period getCarePeriod() {
        return carePeriod;
    }

    public void setCarePeriod(Period carePeriod) {
        this.carePeriod = carePeriod;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Carerecipient getCareRecipient() {
        return careRecipient;
    }

    public void setCareRecipient(Carerecipient careRecipient) {
        this.careRecipient = careRecipient;
    }

    public ConversationPartner getConversationPartner() {
        return conversationPartner;
    }

    public void setConversationPartner(ConversationPartner conversationPartner) {
        this.conversationPartner = conversationPartner;
    }

    public Boolean getLegalAcknowledgement() {
        return legalAcknowledgement;
    }

    public void setLegalAcknowledgement(Boolean legalAcknowledgement) {
        this.legalAcknowledgement = legalAcknowledgement;
    }

    public Boolean getCareDurationMin6Months() {
        return careDurationMin6Months;
    }

    public void setCareDurationMin6Months(Boolean careDurationMin6Months) {
        this.careDurationMin6Months = careDurationMin6Months;
    }

    public Boolean getHomeCare() {
        return isHomeCare;
    }

    public void setHomeCare(Boolean homeCare) {
        isHomeCare = homeCare;
    }

    public ReplacementCare getReplacementCare() {
        return replacementCare;
    }

    public void setReplacementCare(ReplacementCare replacementCare) {
        this.replacementCare = replacementCare;
    }

    public Caregiver getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(Caregiver caregiver) {
        this.caregiver = caregiver;
    }

    @Override
public String toString() {
    return "FormData{" +
            "chatbotMessage='" + chatbotMessage + '\'' +
            ", conversationPartner=" + conversationPartner +
            ", careType=" + careType +
            ", careLevel=" + careLevel +
            ", carePeriod=" + carePeriod +
            ", reason=" + reason +
            ", careRecipient=" + careRecipient +
            ", caregiver=" + caregiver +
            ", replacementCare=" + replacementCare +
            ", isHomeCare=" + isHomeCare +
            ", careDurationMin6Months=" + careDurationMin6Months +
            ", legalAcknowledgement=" + legalAcknowledgement +
            '}';
}


}
