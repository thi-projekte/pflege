package de.pflegital.chatbot.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class BirthdateTool {
    private static final Logger LOG = getLogger(BirthdateTool.class);

    // valide Formate für das Geburtsdatum
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    };

    @Tool("Ermittelt ob ein Geburtsdatum gültig ist oder nicht.")
    public boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || birthdate.trim().isEmpty()) {
            LOG.error("Das Geburtsdatum darf nicht leer sein!");
            return false;
        }

        // Versuche das Datum in verschiedenen Formaten zu parsen
        LocalDate parsedDate = null;
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                parsedDate = LocalDate.parse(birthdate.trim(), formatter);
                // Wenn erfolgreich, verlasse die Schleife
                break;
            } catch (DateTimeParseException e) {
                // Versuche das nächste Format
            }
        }
        if (parsedDate == null) {
            LOG.error("Das Geburtsdatum hat kein gültiges Format!");
            return false;
        }
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusMonths(6); // Frühestes Geburtsdatum: 6 Monate vor jetzt (da Person mind. 6 Monate
                                                // gepflegt werden muss um Verhinderungspflege zu beantragen)
        LocalDate maxDate = now.minusYears(120); // Spätestes Geburtsdatum: 120 Jahre vor jetzt
        // Prüfe ob das Datum zwischen minDate und maxDate liegt
        if (parsedDate.isAfter(minDate)) {
            LOG.error("Das Geburtsdatum muss mindestens 6 Monate in der Vergangenheit liegen!");
            return false;
        }
        if (parsedDate.isBefore(maxDate)) {
            LOG.error("Das Geburtsdatum darf nicht älter als 120 Jahre sein!");
            return false;
        }
        return true;
    }
}
