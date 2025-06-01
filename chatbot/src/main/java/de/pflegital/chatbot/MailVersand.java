package de.pflegital.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MailVersand {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) {
        // .env laden
        Dotenv dotenv = Dotenv.configure()
                .directory("chatbot")
                .filename(".env")
                .load();
        
        try {
            String jsonString = loadJsonFromResources("ExampleFormData.json");
            // JSON parsen
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode formData = rootNode.get("formData");
            
            // HTML Email generieren
            String htmlContent = generateHtmlEmail(formData);
            
            // Email versenden
            sendEmail(dotenv, htmlContent);
            
        } catch (IOException e) {
            System.err.println("Fehler beim Parsen des JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateHtmlEmail(JsonNode formData) throws IOException {
        // HTML Template aus Datei laden
        // Stelle sicher, dass die Datei im Ressourcen-Ordner liegt (src/main/resources)
        String template = loadHtmlTemplate("chatbot/email-template.html");
        
        // Platzhalter mit Daten ersetzen
        Map<String, String> placeholders = createPlaceholderMap(formData);
        
        String htmlContent = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return htmlContent;
    }
    
    private static Map<String, String> createPlaceholderMap(JsonNode formData) {
        Map<String, String> placeholders = new HashMap<>();
        
        // Allgemeine Informationen
        placeholders.put("careType", getSafeString(formData, "careType"));
        placeholders.put("careLevel", String.valueOf(formData.get("careLevel").asInt()));
        placeholders.put("reason", getSafeString(formData, "reason"));
        
        // Pflegezeitraum
        JsonNode carePeriod = formData.get("carePeriod");
        placeholders.put("careStart", formatDate(getSafeString(carePeriod, "careStart")));
        placeholders.put("careEnd", formatDate(getSafeString(carePeriod, "careEnd")));
        
        // Versicherte Person
        JsonNode insuredPerson = formData.get("insuredPerson");
        placeholders.put("insuredName", getSafeString(insuredPerson, "fullName"));
        placeholders.put("insuredBirthDate", formatDate(getSafeString(insuredPerson, "birthDate")));
        placeholders.put("insuredPhone", getSafeString(insuredPerson, "phoneNumber"));
        placeholders.put("insuranceNumber", getSafeString(insuredPerson, "insuranceNumber"));
        placeholders.put("insuredAddress", formatAddress(insuredPerson.get("insuredAddress")));
        
        // Pflegende Person
        JsonNode caregiver = formData.get("caregiver");
        placeholders.put("caregiverName", getSafeString(caregiver, "caregiverName"));
        placeholders.put("caregiverStartDate", formatDate(getSafeString(caregiver, "careStartDate")));
        placeholders.put("caregiverPhone", getSafeString(caregiver, "caregiverPhoneNumber"));
        placeholders.put("caregiverAddress", formatAddress(caregiver.get("caregiverAddress")));
        
        // Ersatzpflege
        JsonNode replacementCare = formData.get("replacementCare");
        String providerName = "";
        String providerAddress = "";
        if (replacementCare.get("provider") != null) {
            providerName = getSafeString(replacementCare.get("provider"), "providerName");
            providerAddress = formatAddress(replacementCare.get("provider").get("providerAddress"));
        }
        placeholders.put("providerName", providerName);
        placeholders.put("providerAddress", providerAddress);
        
        // Zusätzliche Angaben
        placeholders.put("isHomeCare", formData.get("isHomeCare").asBoolean() ? "Ja" : "Nein");
        placeholders.put("careDurationMin6Months", formData.get("careDurationMin6Months").asBoolean() ? "Ja" : "Nein");
        placeholders.put("legalAcknowledgement", formData.get("legalAcknowledgement").asBoolean() ? "Ja" : "Nein");
        
        return placeholders;
    }
    
    private static String loadHtmlTemplate(String fileName) throws IOException {
        try (var inputStream = MailVersand.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("HTML Template nicht gefunden: " + fileName);
            }
            
            StringBuilder content = new StringBuilder();
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append('\n');
                }
            }
            
            System.out.println("HTML Template erfolgreich geladen: " + fileName);
            return content.toString();
        }
    }
    
    private static void sendEmail(Dotenv dotenv, String htmlContent) {
        Resend resend = new Resend(dotenv.get("RESEND_API_KEY"));
        String receiver = "nip6168@thi.de";
        
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Pflegital <test@pflegital.de>")
                .to(receiver)
                .subject("Antrag auf Verhinderungspflege - Pflegital.de")
                .html(htmlContent)
                .build();
        
        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email erfolgreich versendet. ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Fehler beim Versenden der Email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String getSafeString(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : "";
    }
    
    private static String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(dateString);
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return dateString;
        }
    }
    
    private static String formatAddress(JsonNode addressNode) {
        if (addressNode == null) {
            return "";
        }
        
        String street = getSafeString(addressNode, "street");
        String houseNumber = addressNode.get("houseNumber") != null ? 
            String.valueOf(addressNode.get("houseNumber").asInt()) : "";
        String zip = addressNode.get("zip") != null ? 
            String.valueOf(addressNode.get("zip").asInt()) : "";
        String city = getSafeString(addressNode, "city");
        
        return String.format("%s %s, %s %s", street, houseNumber, zip, city).trim();
    }
    
    private static String loadJsonFromResources(String fileName) throws IOException {
        try (var inputStream = MailVersand.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Datei nicht gefunden: " + fileName);
            }
            
            StringBuilder content = new StringBuilder();
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append('\n');
                }
            }
            
            System.out.println("JSON-Datei erfolgreich geladen: " + fileName);
            return content.toString();
        }
    }
}