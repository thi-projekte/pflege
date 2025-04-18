package org;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;
import org.model.CareType;
import org.model.Period;
import org.model.Reason;
import org.model.Address;
import org.model.Caregiver;
import org.model.InsuredPerson;
import org.model.ReplacementCare;

public class FormData {

    // prüft, ob alle FormData schon ausgefüllt / erfragt wurden
    public boolean isComplete() {
        return
                careLevel != null && careLevel >= 2 && careLevel <= 5 &&
                careType != null &&
                // carePeriod
                carePeriod != null && carePeriod.isValid() &&
                // reason
                reason != null &&
                insuredPerson != null && insuredPerson.isValid() &&
                caregiver != null && caregiver.isValid() &&
                replacementCare != null && replacementCare.isValid() &&
                isHomeCare != null && Boolean.TRUE.equals(isHomeCare) &&
                careDurationMin6Months != null && Boolean.TRUE.equals(careDurationMin6Months) &&
                legalAcknowledgement != null && Boolean.TRUE.equals(legalAcknowledgement);
    }

    @JsonProperty("chatbotMessage")
    private String chatbotMessage;

    @JsonProperty("careType")
    @Description("Describes the type of care. Either hourly or daily.")
    private CareType careType;


    @JsonProperty("careLevel")
    @Description("Indicates the care level (Pflegegrad) of a person. Can range from 1 to 5.")
    private Integer careLevel;


    @JsonProperty("carePeriod")
    @Description("Indicates the careStart and careEnd in LocalDate format in YYYY-MM-DD")
    private Period carePeriod;

    @JsonProperty("reason")
    @Description("Describes the reason for the care. Either holiday or other.")
    private Reason reason;

    @JsonProperty("insuredPerson")
    @Description("Information about the insured person, including full name, birth date, address, phone number and insurance number.")
    private InsuredPerson insuredPerson;


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
}