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
class BirthdateToolTest {

    @Inject
    BirthdateTool birthdateTool;

    private static final DateTimeFormatter GERMAN_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter SLASH_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Test
    void testNullBirthdate() {
        assertFalse(birthdateTool.isValidBirthdate(null));
    }

    @Test
    void testEmptyBirthdate() {
        assertFalse(birthdateTool.isValidBirthdate(""));
        assertFalse(birthdateTool.isValidBirthdate("   "));
    }

    @Test
    void testInvalidBirthdateRanges() {
        LocalDate now = LocalDate.now();

        // zukünftiges Datum
        String futureDate = now.plusDays(1).format(GERMAN_FORMATTER);
        assertFalse(birthdateTool.isValidBirthdate(futureDate), "Zukünftiges Datum ist ungültig");

        // Datum vor 6 Monaten (=> ausgehend davon, dass die Person mindestens 6 Monate
        // gepflegt werden muss um Verhinderungspflege zu beantragen ist das max.
        // Geburtsdatum 6 Monate vor dem jetzigen Datum)
        String tooRecentDate = now.minusMonths(5).format(GERMAN_FORMATTER);
        assertFalse(birthdateTool.isValidBirthdate(tooRecentDate), "Datum vor 6 Monaten ist ungültig");

        // Geburtsdatum vor 120 Jahren
        String tooOldDate = now.minusYears(121).format(GERMAN_FORMATTER);
        assertFalse(birthdateTool.isValidBirthdate(tooOldDate), "Datum vor 120 Jahren ist ungültig");
    }

    @Test
    void testValidBirthdateFormats() {
        LocalDate validDate = LocalDate.now().minusYears(30);

        // Test all supported formats
        String germanFormat = validDate.format(GERMAN_FORMATTER);
        String isoFormat = validDate.format(ISO_FORMATTER);
        String slashFormat = validDate.format(SLASH_FORMATTER);

        assertTrue(birthdateTool.isValidBirthdate(germanFormat), "German format wird erkannt");
        assertTrue(birthdateTool.isValidBirthdate(isoFormat), "ISO format should be valid");
        assertTrue(birthdateTool.isValidBirthdate(slashFormat), "Slash format should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "32.01.2020", // Falscher Tag
            "01.13.2020", // Falscher Monat
            "01.01.20", // Falsches Jahr
            "2020/01/01", // Falsches Format
            "01-01-2020" // Falsches Format
    })
    void testInvalidBirthdateFormats(String birthdate) {
        assertFalse(birthdateTool.isValidBirthdate(birthdate));
    }

}
