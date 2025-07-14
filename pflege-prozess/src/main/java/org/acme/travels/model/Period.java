package org.acme.travels.model;

import java.time.LocalDate;

public class Period {

    private LocalDate careStart;
    private LocalDate careEnd;

    public Period() {
        // Leerer Konstruktor für z.B. JSON-Deserialization
    }

    public Period(LocalDate careStart, LocalDate careEnd) {
        this.careStart = careStart;
        this.careEnd = careEnd;
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
