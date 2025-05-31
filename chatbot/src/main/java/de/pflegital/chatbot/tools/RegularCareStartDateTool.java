package de.pflegital.chatbot.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class RegularCareStartDateTool {
    private static final Logger LOG = LoggerFactory.getLogger(RegularCareStartDateTool.class);
    
    //valide Formate für das Pflegebeginn-Datum
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    @Tool("Validates if the regular care start date is valid. The date must be at least 6 months in the past.")
    public boolean validateRegularCareStartDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            LOG.error("Pflegebeginn-Datum ist leer");
            return false;
        }

        LocalDate careStartDate = null;
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                careStartDate = LocalDate.parse(dateStr.trim(), formatter);
                break;
            } catch (DateTimeParseException e) {
                LOG.debug("Datum konnte nicht im Format {} geparst werden: {}", formatter, dateStr);
            }
        }

        if (careStartDate == null) {
            LOG.error("Pflegebeginn-Datum hat kein gültiges Format: {}", dateStr);
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate sixMonthsAgo = today.minus(6, ChronoUnit.MONTHS);

        if (careStartDate.isAfter(today)) {
            LOG.error("Pflegebeginn-Datum liegt in der Zukunft: {}", careStartDate);
            return false;
        }

        if (careStartDate.isAfter(sixMonthsAgo)) {
            LOG.error("Pflegebeginn-Datum liegt weniger als 6 Monate in der Vergangenheit: {}", careStartDate);
            return false;
        }

        LOG.info("Pflegebeginn-Datum ist gültig: {}", careStartDate);
        return true;
    }
}
