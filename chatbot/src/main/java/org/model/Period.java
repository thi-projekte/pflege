package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Period {

    @JsonProperty("careStart")
    private LocalDate careStart;

    @JsonProperty("careEnd")
    private LocalDate careEnd;

    public boolean isValid() {
        // Prüft, ob careStart und careEnd ausgefüllt sind und ob careStart vor careEnd liegt
        return careStart != null && careEnd != null && careStart.isBefore(careEnd);
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
