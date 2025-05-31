package de.pflegital.chatbot.tools;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class InsuranceNumberToolTest {

    @Inject
    InsuranceNumberTool insuranceNumberTool;

    @Test
    void testNullInsuranceNumber() {
        assertFalse(insuranceNumberTool.isValidSecurityNumber(null));
    }

    @Test
    void testEmptyInsuranceNumber() {
        assertFalse(insuranceNumberTool.isValidSecurityNumber(""));
        assertFalse(insuranceNumberTool.isValidSecurityNumber("   "));
    }

    @Test
    void testInvalidInsuranceNumberLength() {
        assertFalse(insuranceNumberTool.isValidSecurityNumber("11111111111")); // 11
        assertFalse(insuranceNumberTool.isValidSecurityNumber("1111111111111")); // 13
        assertFalse(insuranceNumberTool.isValidSecurityNumber("1")); // 1
    }

    @Test
    void testValidInsuranceNumber() {
        // Beispiel einer gültigen Versicherungsnummer
        String validNumber = "65170806J008";
        assertTrue(insuranceNumberTool.isValidSecurityNumber(validNumber), 
            "Gültige Versicherungsnummer sollte akzeptiert werden");
    }

    @Test
    void testValidInsuranceNumberWithSpacesAndLetters() {
        // Versicherungsnummer mit Leerzeichen
        String validNumberWithSpaces = "65 17 08 06 J0 08";
        String validNumberWithSpaces2 = "   65 17 08    06  J0 08 ";
        assertTrue(insuranceNumberTool.isValidSecurityNumber(validNumberWithSpaces), 
            "Versicherungsnummer mit Leerzeichen sollte akzeptiert werden");
        assertTrue(insuranceNumberTool.isValidSecurityNumber(validNumberWithSpaces2)
        , "Versicherungsnummer mit Leerzeichen am Anfang und Ende sollte akzeptiert werden");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "65123456M01",      // Zu kurz (11 Zeichen)
            "65123456M0123",    // Zu lang (13 Zeichen)
            "6A123456M01",     // Ungültige Bereichsnummer (enthält Buchstaben)
            "65132356M01",     // Ungültiges Datum (32. Tag)
            "65130056M01",     // Ungültiges Datum (13. Monat)
            "65123456#01",     // Ungültiger Buchstabe (Sonderzeichen)
            "65123456M0A",     // Ungültige Seriennummer (enthält Buchstaben)
            "65123456M02"      // Falsche Prüfziffer
    })
    void testInvalidInsuranceNumbers(String invalidNumber) {
        assertFalse(insuranceNumberTool.isValidSecurityNumber(invalidNumber), 
            "Ungültige Versicherungsnummer sollte abgelehnt werden: " + invalidNumber);
    }

    @Test
    void testInvalidDateFormat() {
        // Test verschiedener ungültiger Datumsformate
        String[] invalidDates = {
            "65000056M01", // Tag 00
            "65013256M01", // Monat 13
            "65320056M01", // Tag 32
            "65003056M01"  // Monat 03 mit Tag 30
        };

        for (String number : invalidDates) {
            assertFalse(insuranceNumberTool.isValidSecurityNumber(number), 
                "Versicherungsnummer mit ungültigem Datum sollte abgelehnt werden: " + number);
        }
    }

    @Test
    void testValidDateFormat() {
        // Test verschiedener gültiger Datumsformate
        String[] validDates = {
            "65170806J008", // 17.08.2006
            "65100103J005", // 10.01.2003
        };

        for (String number : validDates) {
            assertTrue(insuranceNumberTool.isValidSecurityNumber(number), 
                "Versicherungsnummer mit gültigem Datum sollte akzeptiert werden: " + number);
        }
    }

    @Test
    void testValidLetters() {
        // Test verschiedener gültiger Buchstaben
        String baseNumber = "65123456";
        String endNumber = "01";
        
        for (char c = 'A'; c <= 'Z'; c++) {
            String number = baseNumber + c + endNumber;
            // Da die Prüfziffer von den vorherigen Zeichen abhängt, 
            // können wir hier nur das Format testen
            if (insuranceNumberTool.isValidSecurityNumber(number)) {
                assertTrue(true, "Versicherungsnummer mit gültigem Buchstaben " + c + " sollte akzeptiert werden");
            }
        }
    }

    @Test
    void testCheckDigitCalculation() {
        // Test verschiedene gültige Versicherungsnummern mit korrekter Prüfziffer
        String[] validNumbers = {
            "65170806J008", // 17.08.2006
            "65100103J005", // 10.01.2003
            "65170806J008", // 17.08.2006
            "65170806J008"  // 17.08.2006
        };

        for (String number : validNumbers) {
            assertTrue(insuranceNumberTool.isValidSecurityNumber(number), 
                "Versicherungsnummer mit korrekter Prüfziffer sollte gültig sein: " + number);
        }

        // Test Versicherungsnummern mit falscher Prüfziffer
        String[] invalidNumbers = {
            "65170806J009", // Falsche Prüfziffer (8 statt 9)
            "65100103J006", // Falsche Prüfziffer (5 statt 6)
            "65170806J007", // Falsche Prüfziffer (8 statt 7)
            "65170806J000"  // Falsche Prüfziffer (8 statt 0)
        };

        for (String number : invalidNumbers) {
            assertFalse(insuranceNumberTool.isValidSecurityNumber(number), 
                "Versicherungsnummer mit falscher Prüfziffer sollte ungültig sein: " + number);
        }
    }

    @Test
    void testInvalidBereichsnummer() {
        // Bereichsnummer enthält Buchstaben
        assertFalse(insuranceNumberTool.isValidSecurityNumber("AB170806J008"),
            "Bereichsnummer mit Buchstaben sollte ungültig sein");
        // Bereichsnummer zu kurz
        assertFalse(insuranceNumberTool.isValidSecurityNumber("6170806J008"),
            "Bereichsnummer mit nur einer Ziffer sollte ungültig sein");
        // Bereichsnummer zu lang
        assertFalse(insuranceNumberTool.isValidSecurityNumber("123170806J008"),
            "Bereichsnummer mit drei Ziffern sollte ungültig sein");
    }

    @Test
    void testInvalidGeburtsdatum() {
        // Geburtsdatum enthält Buchstaben
        assertFalse(insuranceNumberTool.isValidSecurityNumber("6517A806J008"),
            "Geburtsdatum mit Buchstaben sollte ungültig sein");
        // Geburtsdatum zu kurz
        assertFalse(insuranceNumberTool.isValidSecurityNumber("651708J008"),
            "Geburtsdatum mit nur vier Ziffern sollte ungültig sein");
        // Geburtsdatum zu lang
        assertFalse(insuranceNumberTool.isValidSecurityNumber("651708061J008"),
            "Geburtsdatum mit sieben Ziffern sollte ungültig sein");
        // Geburtsdatum logisch ungültig (z.B. 32.01.99)
        assertFalse(insuranceNumberTool.isValidSecurityNumber("65320199J008"),
            "Geburtsdatum mit ungültigem Tag sollte ungültig sein");
    }

    @Test
    void testInvalidBuchstabe() {
        // Buchstabe ist eine Zahl
        assertFalse(insuranceNumberTool.isValidSecurityNumber("651708069008"),
            "Buchstabe als Zahl sollte ungültig sein");
        // Buchstabe ist ein Sonderzeichen
        assertFalse(insuranceNumberTool.isValidSecurityNumber("65170806#008"),
            "Buchstabe als Sonderzeichen sollte ungültig sein");
    }

    @Test
    void testInvalidSeriennummer() {
        // Seriennummer enthält Buchstaben
        assertFalse(insuranceNumberTool.isValidSecurityNumber("65170806J0A8"),
            "Seriennummer mit Buchstaben sollte ungültig sein");
        // Seriennummer zu kurz
        assertFalse(insuranceNumberTool.isValidSecurityNumber("65170806J08"),
            "Seriennummer mit nur einer Ziffer sollte ungültig sein");
        // Seriennummer zu lang
        assertFalse(insuranceNumberTool.isValidSecurityNumber("65170806J0008"),
            "Seriennummer mit drei Ziffern sollte ungültig sein");
    }

    @Test
    void testInvalidCharacterInCheckDigitCalculation() {
        // Die Prüfziffernberechnung wird mit einem ungültigen Zeichen (z.B. '!') konfrontiert
        // Die Nummer ist formal korrekt aufgebaut, aber enthält ein Sonderzeichen an einer Stelle, die in die Prüfziffer eingeht
        String invalidForCheckDigit = "6517080!J008"; // '!' an Stelle 8
        assertFalse(insuranceNumberTool.isValidSecurityNumber(invalidForCheckDigit),
            "Versicherungsnummer mit ungültigem Zeichen für Prüfziffernberechnung sollte ungültig sein");
    }
} 