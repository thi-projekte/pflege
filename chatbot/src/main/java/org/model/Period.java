package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Period {

    @JsonProperty("careStart")
    @Description("Start date of the replacement care period. Must be today or a future date.")
    private LocalDate careStart;

    @JsonProperty("careEnd")
    @Description("End date of the replacement care period. Must be on or after careStart and max. 42 days later.")
    private LocalDate careEnd;

    public boolean isCareStartValid() {
        return careStart != null && !careStart.isBefore(LocalDate.now());
    }

    public boolean isCareEndValid() {
        if (careStart == null || careEnd == null) return false;
        long daysBetween = ChronoUnit.DAYS.between(careStart, careEnd);
        return !careEnd.isBefore(careStart) && daysBetween <= 42;
    }

    public boolean isValid() {
        // Prüft, ob careStart und careEnd ausgefüllt sind und ob careStart vor careEnd liegt + die 42 max Tage Differenz
        return isCareStartValid() && isCareEndValid();
    }

    public LocalDate getCareStart() {
        return careStart;
    }

    public void setCareStart(LocalDate careStart) {
        this.careStart = careStart;
    }

    public LocalDate getCareEnd() {
        return careEnd;
    }

    public void setCareEnd(LocalDate careEnd) {
        this.careEnd = careEnd;
    }
}
