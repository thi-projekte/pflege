package de.pflegital.chatbot.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@ApplicationScoped
public class InsuranceNumberTool {
    private static final Logger LOG = getLogger(InsuranceNumberTool.class);

    @Tool("Ermittelt ob eine Versicherungsnummer gültig ist oder nicht.")
    public boolean isValidSecurityNumber(String versicherungsnummer) {

        // Leerzeichen entfernen
        versicherungsnummer = versicherungsnummer.replaceAll("\\s+", "").trim();

        // 1. Länge prüfen
        if (versicherungsnummer.length() != 12) {
            LOG.error("Die Versicherungsnummer hat nicht das korrekte Format (fehlerhafte Länge) !");
            return false;
        }
        // 2. Bestandteile extrahieren
        String bereichsnummer = versicherungsnummer.substring(0, 2);
        String geburtsdatum = versicherungsnummer.substring(2, 8);
        char buchstabe = versicherungsnummer.charAt(8);
        String seriennummer = versicherungsnummer.substring(9, 11);
        char pruefziffer = versicherungsnummer.charAt(11);
        // 3. Bereichsnummer: nur Ziffern
        if (!bereichsnummer.matches("\\d{2}")) {
            LOG.error("Die Versicherungsnummer hat nicht das korrekte Format ( keine gültige Bereichsnummer) !");
            return false;
        }
        // 4. Geburtsdatum prüfen (TTMMJJ)
        if (!geburtsdatum.matches("\\d{6}") || !istGueltigesDatum(geburtsdatum)) {
            LOG.error("Die Versicherungsnummer hat nicht das korrekte Format(kein gültiges Geburtsdatum) !");
            return false;
        }
        // 5. Buchstabe prüfen (nur A–Z)
        if (!Character.isLetter(buchstabe)) {
            LOG.error("Die Versicherungsnummer hat nicht das korrekte Format (Buchstabe an 5. Stelle) !");
            return false;
        }
        // 6. Seriennummer prüfen
        if (!seriennummer.matches("\\d{2}")) {
            LOG.error("Die Versicherungsnummer hat nicht das korrekte Format (Seriennummer) !");
            return false;
        }
        // 7. Prüfziffer berechnen und vergleichen
        String nummerOhnePruefziffer = versicherungsnummer.substring(0, 11);
        char berechnetePruefziffer = berechnePruefziffer(nummerOhnePruefziffer);
        if (pruefziffer != berechnetePruefziffer) {
            LOG.error("Die Versicherungsnummer hat nicht das korrekte Format (Prüfziffer inkorrekt) !");
        }
        return pruefziffer == berechnetePruefziffer;
    }

    private static boolean istGueltigesDatum(String ttmmjj) {
        int tag = Integer.parseInt(ttmmjj.substring(0, 2));
        int monat = Integer.parseInt(ttmmjj.substring(2, 4));
        return monat >= 1 && monat <= 12 && tag >= 1 && tag <= 31;
    }

    private static char berechnePruefziffer(String input) {
        int[] gewichte = { 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2 };
        int summe = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int wert;

            if (Character.isDigit(c)) {
                wert = Character.getNumericValue(c);
            } else if (Character.isLetter(c)) {
                // A=10, ..., Z=35 (offiziell bei Rentenversicherungsnummern)
                wert = Character.toUpperCase(c) - 'A' + 10;
            } else {
                LOG.error("Ungültiges Zeichen in Prüfzifferberechnung: {}", c);
                return 'X'; // Fehler
            }

            int produkt = wert * gewichte[i];
            int quersumme = (produkt / 10) + (produkt % 10);
            summe += quersumme;
        }

        int pruefziffer = (10 - (summe % 10)) % 10;

        return Character.forDigit(pruefziffer, 10);
    }

}
