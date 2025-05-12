package de.pflegital.chatbot;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InsuranceNumberTool {

    @Tool("Ermittelt ob eine Versicherungsnummer gültig ist oder nicht.")
    public boolean isValidSecurityNumber(String versicherungsnummer) {

        // Leerzeichen entfernen
        versicherungsnummer = versicherungsnummer.replaceAll("\\s+", "").trim();

        // 1. Länge prüfen
        if (versicherungsnummer.length() != 12) {
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
            return false;
        }
        // 4. Geburtsdatum prüfen (TTMMJJ)
        if (!geburtsdatum.matches("\\d{6}") || !istGueltigesDatum(geburtsdatum)) {
            return false;
        }
        // 5. Buchstabe prüfen (nur A–Z)
        if (!Character.isLetter(buchstabe)) {
            return false;
        }
        // 6. Seriennummer prüfen
        if (!seriennummer.matches("\\d{2}")) {
            return false;
        }
        // 7. Prüfziffer berechnen und vergleichen
        String nummerOhnePruefziffer = versicherungsnummer.substring(0, 11);
        char berechnetePruefziffer = berechnePruefziffer(nummerOhnePruefziffer);
        return pruefziffer == berechnetePruefziffer;
    }

    private static boolean istGueltigesDatum(String ttmmjj) {
        int tag = Integer.parseInt(ttmmjj.substring(0, 2));
        int monat = Integer.parseInt(ttmmjj.substring(2, 4));
        int jahr = Integer.parseInt(ttmmjj.substring(4, 6));
        if (monat < 1 || monat > 12 || tag < 1 || tag > 31)
            return false;
        // Grobe Prüfung ohne Schaltjahrlogik
        return true;
    }

    private static char berechnePruefziffer(String input) {
        StringBuilder numerisch = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                numerisch.append(c);
            } else if (Character.isLetter(c)) {
                // Buchstaben werden zu Zahlen A=1, B=2, ..., Z=26
                numerisch.append((int) Character.toUpperCase(c) - 64);
            }
        }
        int summe = 0;
        for (char c : numerisch.toString().toCharArray()) {
            summe += Character.getNumericValue(c);
        }
        int pruefziffer = summe % 10;
        return Character.forDigit(pruefziffer, 10);
    }

}
