package de.pflegital.chatbot;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.pflegital.chatbot.model.*;
import dev.langchain4j.model.output.structured.Description;

public class FormData {

    // prüft, ob alle FormData schon ausgefüllt / erfragt wurden
    public boolean isComplete() {
        return careLevel != null && careLevel >= 2 && careLevel <= 5 &&
                careType != null &&
                // carePeriod
                carePeriod != null && carePeriod.isValid() &&
                // reason
                reason != null &&
                careRecipient != null && careRecipient.isValid() &&
                caregiver != null && caregiver.isValid() &&
                replacementCare != null && replacementCare.isValid() &&
                isHomeCare != null && isHomeCare &&
                careDurationMin6Months != null && careDurationMin6Months &&
                legalAcknowledgement != null && legalAcknowledgement;
    }

    @JsonProperty("chatbotMessage")
    private String chatbotMessage;

    @JsonProperty("conversationPartner")
    @Description("Indicates whether the person currently interacting with the chatbot is the care recipient or a relative.")
    private ConversationPartner conversationPartner;

    @JsonProperty("careType")
    @Description("Describes the type of care. Either hourly or daily.")
    private CareType careType;

    @JsonProperty("careLevel")
    @Description("Indicates the care level of a person. Can range from 1 to 5.")
    private Integer careLevel;

    @JsonProperty("carePeriod")
    @Description("Indicates the replacementcareStart and replacementcareEnd of the replacementcare.")
    private Period carePeriod;

    @JsonProperty("reason")
    @Description("Describes the reason for the care. Either holiday or other.")
    private Reason reason;

    @JsonProperty("careRecipient")
    @Description("Information about the care recipient, including full name, birth date, address, phone number and insurance number.")
    private Carerecipient careRecipient;

    @JsonProperty("caregiver")
    @Description("Information about the regular caregiver including name, address, phone number and care start date.")
    private Caregiver caregiver;

    @JsonProperty("replacementCare")
    @Description("Details of the person or organization providing the replacement care. Can be a professional provider or a private person.")
    private ReplacementCare replacementCare;

    @JsonProperty("isHomeCare")
    @Description("Indicates whether the care takes place at home. Must be true.")
    private Boolean isHomeCare;

    @JsonProperty("careDurationMin6Months")
    @Description("Indicates whether care has been provided for at least 6 months. Required field.")
    private Boolean careDurationMin6Months;

    @JsonProperty("legalAcknowledgement")
    @Description("Confirmation that the provided information is truthful. Must be true.")
    private Boolean legalAcknowledgement;

    // Getter & Setter

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
}
