package org;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;
import org.model.*;

public class FormData {


    // wird von AIResource genutzt, um der KI die aktuellen Daten mitzuteilen.
    @Override
    public String toString() {
        return String.format(
                "{careLevel: %s, careType: %s, carePeriod: %s, reason: %s}",
                careLevel != null ? careLevel : "not provided",
                careType != null ? careType.toString() : "not provided",
                carePeriod != null ? carePeriod.getCareStart() + " bis " + carePeriod.getCareEnd() : "not provided",
                reason != null ? reason.toString() : null
        );
    }

    // prüft, ob alle FormData schon ausgefüllt / erfragt wurden
    public boolean isComplete() {
        return //carelevel:
                careLevel != null && careLevel >= 1 && careLevel <= 5 &&
                        // careType:
                        careType != null &&
        // carePeriod:
            carePeriod.isValid() &&
                        // reason:
                        reason != null;
    }


    @JsonProperty("careType")
    @Description("Describes the type of care. Either hourly or daily.")
    private CareType careType;

    @JsonProperty("chatbotMessage")
    private String chatbotMessage;

    @JsonProperty("careLevel")
    @Description("Indicates the care level (Pflegegrad) of a person. Can range from 1 to 5.")
    private Integer careLevel;


    @JsonProperty("carePeriod")
    @Description("Indicates the careStart and careEnd in LocalDate format in YYYY-MM-DD")
    private Period carePeriod;

    @JsonProperty("reason")
    @Description("Describes the reason for the care. Either holiday or other.")
    private Reason reason;

    //@JsonProperty("insuredPerson")
    //private InsuredPerson insuredPerson;



//    @JsonProperty("caregiver")
//    private Caregiver caregiver;

//    @JsonProperty("replacementCare")
//    private ReplacementCare replacementCare;
//
//    @JsonProperty("isHomeCare")
//    private Boolean isHomeCare;
//
//    @JsonProperty("careDurationMin6Months")
//    private Boolean careDurationMin6Months;
//
//    @JsonProperty("legalAcknowledgement")
//    private Boolean legalAcknowledgement;
//
//
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