package de.pflegital.chatbot.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class RegularCareStartDateTool {
    private static final Logger LOG = getLogger(RegularCareStartDateTool.class);
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    };

    @Tool("Ermittelt ob ein Pflegebeginn gültig ist oder nicht. Das Datum muss in der Vergangenheit liegen und darf nicht älter als 6 Monate sein.")
    public String isValidCareStartDate(String startDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            return "Das Pflegebeginn-Datum darf nicht leer sein!";
        }

        // Versuche das Datum in verschiedenen Formaten zu parsen
        LocalDate parsedDate = parseDate(startDate);
        if (parsedDate == null) {
            return "Das Pflegebeginn-Datum hat kein gültiges Format!";
        }

        LocalDate now = LocalDate.now();
        LocalDate sixMonthsAgo = now.minusMonths(6);

        // Prüfe ob das Datum in der Vergangenheit liegt
        if (!parsedDate.isBefore(now)) {
            return "Das Pflegebeginn-Datum muss in der Vergangenheit liegen!";
        }

        // Prüfe ob das Datum nicht älter als 6 Monate ist
        if (parsedDate.isBefore(sixMonthsAgo)) {
            return "Das Pflegebeginn-Datum darf nicht älter als 6 Monate sein!";
        }

        return "valid";
    }

    private LocalDate parseDate(String date) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(date.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Versuche das nächste Format
                continue;
            }
        }
        return null;
    }
}
