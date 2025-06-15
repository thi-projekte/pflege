package de.pflegital.chatbot;

// Korrekte Imports für Ihre Model-Struktur
import de.pflegital.chatbot.model.Address;
import de.pflegital.chatbot.model.CareType;
import de.pflegital.chatbot.model.Carerecipient;
import de.pflegital.chatbot.model.Caregiver;
import de.pflegital.chatbot.model.Period;
import de.pflegital.chatbot.model.Reason;
import de.pflegital.chatbot.model.ReplacementCare;
import de.pflegital.chatbot.model.replacementcare.Provider;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MailVersand {

    // Für Kogito
    public static void sendFormDataEmail(FormData formData, Dotenv dotenv) throws IOException, ResendException {
        String htmlContent = generateHtmlEmail(formData);
        sendEmail(htmlContent, dotenv);
    }

    // Für Test
    public static void sendApplicationEmail(FormData formData) throws IOException, ResendException {
        Dotenv dotenv = Dotenv.configure()
                .directory("chatbot")
                .filename(".env")
                .load();
        sendFormDataEmail(formData, dotenv);
    }

    private static String generateHtmlEmail(FormData formData) throws IOException {
        String template = loadHtmlTemplate("chatbot/email-template.html");
        Map<String, String> placeholders = createPlaceholderMap(formData);

        String htmlContent = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return htmlContent;
    }
    
    private static Map<String, String> createPlaceholderMap(FormData formData) {
        Map<String, String> placeholders = new HashMap<>();

        // Allgemeine Informationen
        placeholders.put("careType", Optional.ofNullable(formData.getCareType()).map(Object::toString).orElse(""));
        placeholders.put("careLevel", Optional.ofNullable(formData.getCareLevel()).map(String::valueOf).orElse(""));
        placeholders.put("reason", Optional.ofNullable(formData.getReason()).map(Object::toString).orElse(""));

        // Pflegezeitraum
        Period carePeriod = formData.getCarePeriod();
        if (carePeriod != null) {
            // KORREKTUR: Getter-Namen an Period-Klasse angepasst
            placeholders.put("careStart", formatDate(carePeriod.getCareStart()));
            placeholders.put("careEnd", formatDate(carePeriod.getCareEnd()));
        } else {
            placeholders.put("careStart", "");
            placeholders.put("careEnd", "");
        }

        // Versicherte Person
        Carerecipient careRecipient = formData.getCareRecipient();
        if (careRecipient != null) {
            placeholders.put("insuredName", careRecipient.getFullName());
            placeholders.put("insuredBirthDate", formatDate(careRecipient.getBirthDate()));
            placeholders.put("insuredPhone", careRecipient.getPhoneNumber());
            placeholders.put("insuranceNumber", careRecipient.getInsuranceNumber());
            placeholders.put("insuredAddress", formatAddress(careRecipient.getInsuredAddress()));
        }

        // Pflegende Person
        Caregiver caregiver = formData.getCaregiver();
        if (caregiver != null) {
            placeholders.put("caregiverName", caregiver.getCaregiverName());
            placeholders.put("caregiverStartDate", formatDate(caregiver.getCareStartDate()));
            placeholders.put("caregiverPhone", caregiver.getCaregiverPhoneNumber());
            placeholders.put("caregiverAddress", formatAddress(caregiver.getCaregiverAddress()));
        }

        // Ersatzpflege
        ReplacementCare replacementCare = formData.getReplacementCare();
        String providerName = "";
        String providerAddress = "";
        if (replacementCare != null && replacementCare.getProvider() != null) {
            Provider provider = replacementCare.getProvider();
            providerName = provider.getProviderName();
            providerAddress = formatAddress(provider.getProviderAddress());
        }
        placeholders.put("providerName", providerName);
        placeholders.put("providerAddress", providerAddress);

        // Zusätzliche Angaben
        placeholders.put("isHomeCare", Optional.ofNullable(formData.getHomeCare()).map(b -> b ? "Ja" : "Nein").orElse("Nein"));
        placeholders.put("careDurationMin6Months", Optional.ofNullable(formData.getCareDurationMin6Months()).map(b -> b ? "Ja" : "Nein").orElse("Nein"));
        placeholders.put("legalAcknowledgement", Optional.ofNullable(formData.getLegalAcknowledgement()).map(b -> b ? "Ja" : "Nein").orElse("Nein"));

        return placeholders;
    }

    // Angepasste sendEmail-Methode, die Dotenv als Parameter erhält
    private static void sendEmail(String htmlContent, Dotenv dotenv) throws ResendException {
        Resend resend = new Resend(dotenv.get("RESEND_API_KEY"));
        String receiver = "adb7838@thi.de";

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Pflegital <test@pflegital.de>")
                .to(receiver)
                .subject("Antrag auf Verhinderungspflege - Pflegital.de")
                .html(htmlContent)
                .build();

        CreateEmailResponse data = resend.emails().send(params);
        System.out.println("E-Mail erfolgreich versendet. ID: " + data.getId());
    }

    private static String loadHtmlTemplate(String fileName) throws IOException {
        try (var inputStream = MailVersand.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) throw new IOException("HTML Template nicht gefunden: " + fileName);
            StringBuilder content = new StringBuilder();
            try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) content.append(line).append('\n');
            }
            System.out.println("HTML Template erfolgreich geladen: " + fileName);
            return content.toString();
        }
    }
    
    private static String formatAddress(Address address) {
        if (address == null) return "";
        return String.format("%s %s, %s %s", 
            address.getStreet(), address.getHouseNumber(), address.getZip(), address.getCity()).trim();
    }
    
    private static String formatDate(LocalDate date) {
        if (date == null) return "";
        try {
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return date.toString();
        }
    }

    // --- Methoden nur für Testzwecke ---

    public static void main(String[] args) {
        System.out.println("Starte Testlauf für den E-Mail-Versand mit direktem FormData-Objekt...");
        try {
            // Direkte Erstellung des FormData-Objekts
            FormData formData = createTestFormData();
            
            // Debug-Ausgabe
            System.out.println("=== DEBUG: FormData Inhalt ===");
            System.out.println("CareType: " + formData.getCareType());
            System.out.println("CareLevel: " + formData.getCareLevel());
            if (formData.getCareRecipient() != null) {
                System.out.println("Versicherte Person: " + formData.getCareRecipient().getFullName());
                System.out.println("Telefon: " + formData.getCareRecipient().getPhoneNumber());
                System.out.println("Versicherungsnummer: " + formData.getCareRecipient().getInsuranceNumber());
            }
            if (formData.getCaregiver() != null) {
                System.out.println("Pflegende Person: " + formData.getCaregiver().getCaregiverName());
            }
            if (formData.getReplacementCare() != null && formData.getReplacementCare().getProvider() != null) {
                System.out.println("Anbieter: " + formData.getReplacementCare().getProvider().getProviderName());
            }
            
            // E-Mail versenden
            sendApplicationEmail(formData);
            System.out.println("Testlauf erfolgreich abgeschlossen.");
            
        } catch (IOException | ResendException e) {
            System.err.println("Fehler während des Testlaufs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Erstellt ein Test-FormData-Objekt mit Beispielwerten.
     * Diese Methode dient nur zu Testzwecken und sollte nicht in der Produktion verwendet werden.
     */
    private static FormData createTestFormData() {
        FormData formData = new FormData();
        
        // Grundlegende Informationen
        formData.setCareType(CareType.STUNDENWEISE);
        formData.setCareLevel(3);
        formData.setReason(Reason.URLAUB);
        formData.setHomeCare(true);
        formData.setCareDurationMin6Months(true);
        formData.setLegalAcknowledgement(true);
        formData.setChatbotMessage("Test-Nachricht");
        
        // Pflegezeitraum
        Period carePeriod = new Period();
        carePeriod.setCareStart(LocalDate.of(2025, 11, 10));
        carePeriod.setCareEnd(LocalDate.of(2025, 12, 21));
        formData.setCarePeriod(carePeriod);
        
        // Versicherte Person (Care Recipient)
        Carerecipient careRecipient = new Carerecipient();
        careRecipient.setFullName("Max Mustermann");
        careRecipient.setBirthDate(LocalDate.of(1970, 8, 9));
        careRecipient.setPhoneNumber("0157 32352131");
        careRecipient.setInsuranceNumber("985167234123");
        
        // Adresse der versicherten Person
        Address insuredAddress = new Address();
        insuredAddress.setStreet("Fraunhoferstraße");
        insuredAddress.setHouseNumber(30);
        insuredAddress.setZip("80469");
        insuredAddress.setCity("München");
        careRecipient.setInsuredAddress(insuredAddress);
        
        formData.setCareRecipient(careRecipient);
        
        // Pflegende Person (Caregiver)
        Caregiver caregiver = new Caregiver();
        caregiver.setCaregiverName("Manuel Meier");
        caregiver.setCareStartDate(LocalDate.of(2025, 11, 10));
        caregiver.setCaregiverPhoneNumber("0157 323512311");
        
        // Adresse der pflegenden Person
        Address caregiverAddress = new Address();
        caregiverAddress.setStreet("Fraunhoferstraße");
        caregiverAddress.setHouseNumber(30);
        caregiverAddress.setZip("80469");
        caregiverAddress.setCity("München");
        caregiver.setCaregiverAddress(caregiverAddress);
        
        formData.setCaregiver(caregiver);
        
        // Ersatzpflege
        ReplacementCare replacementCare = new ReplacementCare();
        Provider provider = new Provider();
        provider.setProviderName("Professional Pflege GmbH");
        
        // Adresse des Anbieters
        Address providerAddress = new Address();
        providerAddress.setStreet("Esplanade");
        providerAddress.setHouseNumber(12);
        providerAddress.setZip("80469");
        providerAddress.setCity("München");
        provider.setProviderAddress(providerAddress);
        
        replacementCare.setProvider(provider);
        formData.setReplacementCare(replacementCare);
        
        return formData;
    }
}