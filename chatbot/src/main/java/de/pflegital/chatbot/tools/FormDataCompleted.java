package de.pflegital.chatbot.tools;

import de.pflegital.chatbot.FormData;
import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class FormDataCompleted {

    @Tool("Prüft, ob alle Pflichtfelder im Formular ausgefüllt und gültig sind. Gibt eine Liste der fehlenden oder ungültigen Felder zurück.")
    public String checkFormData(FormData formData) {
        if (formData.getCareLevel() == null || formData.getCareLevel() < 2 || formData.getCareLevel() > 5) {
            return "Pflegegrad fehlt oder ist ungültig.";
        }
        if (formData.getCareType() == null) {
            return "Art der Pflege fehlt.";
        }
        if (formData.getCarePeriod() == null || !formData.getCarePeriod().isValid()) {
            return "Zeitraum fehlt oder ist ungültig.";
        }
        if (formData.getReason() == null) {
            return "Grund der Pflege fehlt.";
        }
        if (formData.getCareRecipient() == null || !formData.getCareRecipient().isValid()) {
            return "Angaben zur pflegebedürftigen Person fehlen oder sind ungültig.";
        }
        if (formData.getCaregiver() == null || !formData.getCaregiver().isValid()) {
            return "Angaben zur regulären Pflegekraft fehlen oder sind ungültig.";
        }
        if (formData.getReplacementCare() == null || !formData.getReplacementCare().isValid()) {
            return "Angaben zur Ersatzpflege fehlen oder sind ungültig.";
        }
        return "Alle erforderlichen Felder sind ausgefüllt und gültig!";
    }
}
