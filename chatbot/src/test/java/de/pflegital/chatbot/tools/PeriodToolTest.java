package de.pflegital.chatbot.tools;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PeriodToolTest {

    @Inject
    PeriodTool periodTool;

    private static final DateTimeFormatter GERMAN_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter GERMAN_SHORT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    void testNullDates() {
        assertFalse(periodTool.isValidPeriod(null, "01.01.2024"));
        assertFalse(periodTool.isValidPeriod("01.01.2024", null));
        assertFalse(periodTool.isValidPeriod(null, null));
    }

    @Test
    void testEmptyDates() {
        assertFalse(periodTool.isValidPeriod("", "01.01.2024"));
        assertFalse(periodTool.isValidPeriod("01.01.2024", ""));
        assertFalse(periodTool.isValidPeriod("   ", "01.01.2024"));
        assertFalse(periodTool.isValidPeriod("01.01.2024", "   "));
        assertFalse(periodTool.isValidPeriod("   ", "   "));
    }

    @Test
    void testPastStartDate() {
        LocalDate now = LocalDate.now();
        String pastDate = now.minusDays(1).format(GERMAN_FORMATTER);
        String futureDate = now.plusDays(10).format(GERMAN_FORMATTER);

        assertFalse(periodTool.isValidPeriod(pastDate, futureDate),
                "Startdatum in der Vergangenheit sollte ungültig sein");
    }

    @Test
    void testEndDateBeforeStartDate() {
        LocalDate now = LocalDate.now();
        String startDate = now.plusDays(10).format(GERMAN_FORMATTER);
        String endDate = now.plusDays(5).format(GERMAN_FORMATTER);

        assertFalse(periodTool.isValidPeriod(startDate, endDate),
                "Enddatum vor Startdatum sollte ungültig sein");
    }

    @Test
    void testPeriodTooLong() {
        LocalDate now = LocalDate.now();
        String startDate = now.plusDays(1).format(GERMAN_FORMATTER);
        String endDate = now.plusDays(43).format(GERMAN_FORMATTER);

        assertFalse(periodTool.isValidPeriod(startDate, endDate),
                "Zeitraum länger als 42 Tage sollte ungültig sein");
    }

    @Test
    void testValidDateFormats() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.plusDays(1);
        LocalDate endDate = now.plusDays(10);

        // Startdatum testen
        String startGermanFormat = startDate.format(GERMAN_FORMATTER);
        String startGermanShortFormat = startDate.format(GERMAN_SHORT_FORMATTER);
        String startIsoFormat = startDate.format(ISO_FORMATTER);
        String endDateStr = endDate.format(GERMAN_FORMATTER);

        assertTrue(periodTool.isValidPeriod(startGermanFormat, endDateStr),
                "Deutsches Format (dd.MM.yyyy) für Startdatum sollte gültig sein");
        assertTrue(periodTool.isValidPeriod(startGermanShortFormat, endDateStr),
                "Deutsches Kurzformat (dd.MM.yy) für Startdatum sollte gültig sein");
        assertTrue(periodTool.isValidPeriod(startIsoFormat, endDateStr),
                "ISO Format für Startdatum sollte gültig sein");

        // Enddatum testen
        String endGermanFormat = endDate.format(GERMAN_FORMATTER);
        String endGermanShortFormat = endDate.format(GERMAN_SHORT_FORMATTER);
        String endIsoFormat = endDate.format(ISO_FORMATTER);
        String startDateStr = startDate.format(GERMAN_FORMATTER);

        assertTrue(periodTool.isValidPeriod(startDateStr, endGermanFormat),
                "Deutsches Format (dd.MM.yyyy) für Enddatum sollte gültig sein");
        assertTrue(periodTool.isValidPeriod(startDateStr, endGermanShortFormat),
                "Deutsches Kurzformat (dd.MM.yy) für Enddatum sollte gültig sein");
        assertTrue(periodTool.isValidPeriod(startDateStr, endIsoFormat),
                "ISO Format für Enddatum sollte gültig sein");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "32.01.2024", // Ungültiger Tag
            "01.13.2024", // Ungültiger Monat
            "01.01.201", // Ungültiges Jahr
            "2024/01/01", // Ungültiges Format
            "01-01-2024", // Ungültiges Format
            "2024.01.01" // Ungültiges Format
    })
    void testInvalidStartDateFormats(String invalidDate) {
        LocalDate now = LocalDate.now();
        String validEndDate = now.plusDays(10).format(GERMAN_FORMATTER);
        assertFalse(periodTool.isValidPeriod(invalidDate, validEndDate));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "32.01.2024", // Ungültiger Tag
            "01.13.2024", // Ungültiger Monat
            "01.01.201", // Ungültiges Jahr
            "2024/01/01", // Ungültiges Format
            "01-01-2024", // Ungültiges Format
            "2024.01.01" // Ungültiges Format
    })
    void testInvalidEndDateFormats(String invalidDate) {
        LocalDate now = LocalDate.now();
        String validStartDate = now.plusDays(1).format(GERMAN_FORMATTER);
        assertFalse(periodTool.isValidPeriod(validStartDate, invalidDate));
    }

    @Test
    void testValidPeriodExactly42Days() {
        LocalDate now = LocalDate.now();
        String startDate = now.plusDays(1).format(GERMAN_FORMATTER);
        String endDate = now.plusDays(42).format(GERMAN_FORMATTER);

        assertTrue(periodTool.isValidPeriod(startDate, endDate),
                "Zeitraum von genau 42 Tagen sollte gültig sein");
    }

    @Test
    void testValidPeriodLessThan42Days() {
        LocalDate now = LocalDate.now();
        String startDate = now.plusDays(1).format(GERMAN_FORMATTER);
        String endDate = now.plusDays(21).format(GERMAN_FORMATTER);

        assertTrue(periodTool.isValidPeriod(startDate, endDate),
                "Zeitraum von weniger als 42 Tagen sollte gültig sein");
    }

    @Test
    void testSameStartAndEndDate() {
        LocalDate now = LocalDate.now();
        String date = now.plusDays(1).format(GERMAN_FORMATTER);

        assertTrue(periodTool.isValidPeriod(date, date),
                "Gleiches Start- und Enddatum sollte für einen eintägigen Zeitraum gültig sein");
    }
}
