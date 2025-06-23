package de.pflegital.chatbot.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.pflegital.chatbot.FormData;
import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class FormDataCompleted {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String currentDate = LocalDate.now().format(DATE_FORMATTER);

    @Tool("Prüft, ob alle Pflichtfelder im Formular ausgefüllt und gültig sind. Gibt eine Liste der fehlenden oder ungültigen Felder zurück.")
    public String checkFormData(FormData formData) {
        if (formData.getReplacementCare() == null || !formData.getReplacementCare().isValid()) {
            return "Angaben zur Verhinderungspflege (isProfessional = true oder false) fehlen oder sind ungültig.";
        }

        if (formData.getCareLevel() == null || formData.getCareLevel() < 2 || formData.getCareLevel() > 5) {
            return "Pflegegrad der pflegebedürftigen Person fehlt oder ist ungültig.";
        }
        if (formData.getCareType() == null) {
            return "Art der Verhinderunsflege (stundenweise oder tageweise)fehlt.";
        }
        if (formData.getCarePeriod() == null || !formData.getCarePeriod().isValid()) {
            return "Zeitraum der Verhinderungspflege fehlt oder ist ungültig (Start: ab dem aktuellen Datum"
                    + currentDate + " und Ende maximal 42 Tage später).";
        }

        if (formData.getReason() == null) {
            return "Grund der Pflege (Urlaub oder Sonstiges ) fehlt.";
        }
        if (formData.getCareRecipient() == null || !formData.getCareRecipient().isValid()) {
            return "Angaben (Name, Geburtsdatum, Versicherungsnummer, Adresse) zur pflegebedürftigen Person fehlen oder sind ungültig.";
        }
        if (formData.getCaregiver() == null || !formData.getCaregiver().isValid()) {
            return "Angaben zur regulären Pflegekraft (Name, Adresse, Pflegebeginn (mind. 6 Monate in der Vergangenheitfehlen oder sind ungültig.";
        }

        return "Alle erforderlichen Felder sind ausgefüllt und gültig!";
    }
}
