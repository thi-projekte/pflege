package de.pflegital.chatbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Period {

    @JsonProperty("replacementcareStart")
    @Description("Start date of the replacement care period. Must be today or a future date.")
    private LocalDate replacementcareStart;

    @JsonProperty("replacementcareEnd")
    @Description("End date of the replacement care period. Must be on or after careStart and max. 42 days later.")
    private LocalDate replacementcareEnd;

    public boolean isCareStartValid() {
        return replacementcareStart != null && !replacementcareStart.isBefore(LocalDate.now());
    }

    public boolean isCareEndValid() {
        if (replacementcareStart == null || replacementcareEnd == null)
            return false;
        long daysBetween = ChronoUnit.DAYS.between(replacementcareStart, replacementcareEnd);
        return !replacementcareEnd.isBefore(replacementcareStart) && daysBetween <= 42;
    }

    public boolean isValid() {
        // Prüft, ob careStart und careEnd ausgefüllt sind und ob careStart vor careEnd liegt + die 42 max Tage
        // Differenz
        return isCareStartValid() && isCareEndValid();
    }

    public LocalDate getCareStart() {
        return replacementcareStart;
    }

    public void setCareStart(LocalDate replacementcareStart) {
        this.replacementcareStart = replacementcareStart;
    }

    public LocalDate getCareEnd() {
        return replacementcareEnd;
    }

    public void setCareEnd(LocalDate replacementcareEnd) {
        this.replacementcareEnd = replacementcareEnd;
    }
}
