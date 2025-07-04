package de.pflegital.chatbot.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class PeriodTool {
    private static final Logger LOG = getLogger(PeriodTool.class);

    // valide Formate für das Start- und Enddatum
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    @Tool("Ermittelt ob ein Zeitraum für die Verhinderungspflege gültig ist oder nicht.")
    public boolean isValidPeriod(String startDate, String endDate) {
        if (startDate == null || startDate.trim().isEmpty() || endDate == null || endDate.trim().isEmpty()) {
            LOG.error("Start- und Enddatum dürfen nicht leer sein!");
            return false;
        }

        // Versuche die Daten in verschiedenen Formaten zu parsen
        LocalDate parsedStartDate = parseDate(startDate);
        LocalDate parsedEndDate = parseDate(endDate);

        if (parsedStartDate == null || parsedEndDate == null) {
            LOG.error("Mindestens eines der Daten hat kein gültiges Format!");
            return false;
        }

        LocalDate now = LocalDate.now();

        // Prüfe ob das Startdatum heute oder in der Zukunft liegt
        if (parsedStartDate.isBefore(now)) {
            LOG.error("Das Startdatum muss heute oder in der Zukunft liegen!");
            return false;
        }

        // Prüfe ob das Enddatum vor dem Startdatum liegt
        if (parsedEndDate.isBefore(parsedStartDate)) {
            LOG.error("Das Enddatum muss nach dem Startdatum liegen!");
            return false;
        }

        // Prüfe ob der Zeitraum maximal 42 Tage beträgt
        long daysBetween = ChronoUnit.DAYS.between(parsedStartDate, parsedEndDate);
        if (daysBetween >= 42) {
            LOG.error("Der Zeitraum darf maximal 42 Tage betragen!");
            return false;
        }

        return true;
    }

    private LocalDate parseDate(String date) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(date.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Versuche das nächste Format
            }
        }
        return null;
    }
}
