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
class RegularCareStartDateToolTest {

    @Inject
    RegularCareStartDateTool regularCareStartDateTool;

    private static final DateTimeFormatter GERMAN_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter GERMAN_SHORT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    void testNullDate() {
        assertFalse(regularCareStartDateTool.validateRegularCareStartDate(null));
    }

    @Test
    void testEmptyDate() {
        assertFalse(regularCareStartDateTool.validateRegularCareStartDate(""));
        assertFalse(regularCareStartDateTool.validateRegularCareStartDate("   "));
    }

    @Test
    void testFutureDate() {
        LocalDate now = LocalDate.now();
        String futureDate = now.plusDays(1).format(GERMAN_FORMATTER);
        assertFalse(regularCareStartDateTool.validateRegularCareStartDate(futureDate),
                "Zukünftiges Datum sollte ungültig sein");
    }

    @Test
    void testDateLessThanSixMonthsAgo() {
        LocalDate now = LocalDate.now();
        String recentDate = now.minusMonths(5).format(GERMAN_FORMATTER);
        assertFalse(regularCareStartDateTool.validateRegularCareStartDate(recentDate),
                "Datum weniger als 6 Monate in der Vergangenheit sollte ungültig sein");
    }

    @Test
    void testValidDateFormats() {
        LocalDate validDate = LocalDate.now().minusMonths(7);

        // Test all supported formats
        String germanFormat = validDate.format(GERMAN_FORMATTER);
        String germanShortFormat = validDate.format(GERMAN_SHORT_FORMATTER);
        String isoFormat = validDate.format(ISO_FORMATTER);

        assertTrue(regularCareStartDateTool.validateRegularCareStartDate(germanFormat),
                "Deutsches Format (dd.MM.yyyy) sollte gültig sein");
        assertTrue(regularCareStartDateTool.validateRegularCareStartDate(germanShortFormat),
                "Deutsches Kurzformat (dd.MM.yy) sollte gültig sein");
        assertTrue(regularCareStartDateTool.validateRegularCareStartDate(isoFormat),
                "ISO Format sollte gültig sein");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "32.01.2020", // Ungültiger Tag
            "01.13.2020", // Ungültiger Monat
            "01.01.201", // Ungültiges Jahr
            "2020/01/01", // Ungültiges Format
            "01-01-2020", // Ungültiges Format
            "2020.01.01" // Ungültiges Format
    })
    void testInvalidDateFormats(String date) {
        assertFalse(regularCareStartDateTool.validateRegularCareStartDate(date));
    }

    @Test
    void testValidDateExactlySixMonthsAgo() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        String date = sixMonthsAgo.format(GERMAN_FORMATTER);
        assertTrue(regularCareStartDateTool.validateRegularCareStartDate(date),
                "Datum genau 6 Monate in der Vergangenheit sollte gültig sein");
    }

    @Test
    void testValidDateMoreThanSixMonthsAgo() {
        LocalDate oldDate = LocalDate.now().minusMonths(7);
        String date = oldDate.format(GERMAN_FORMATTER);
        assertTrue(regularCareStartDateTool.validateRegularCareStartDate(date),
                "Datum mehr als 6 Monate in der Vergangenheit sollte gültig sein");
    }
}
