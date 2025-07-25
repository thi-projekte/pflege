package org.acme.travels.service;

// Korrekte Imports für Ihre Model-Struktur
import org.acme.travels.model.Address;
import org.acme.travels.model.Carerecipient;
import org.acme.travels.model.Caregiver;
import org.acme.travels.model.Period;
import org.acme.travels.model.FormData;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class MailVersand {
    private static final Logger LOG = LoggerFactory.getLogger(MailVersand.class);

    @ConfigProperty(name = "resend.api.key")
    String resendApiKey;

    public void sendFormDataEmail(FormData formData) throws IOException, ResendException {
        String htmlContent = generateHtmlEmail(formData);
        sendEmail(htmlContent);
    }

    private  String generateHtmlEmail(FormData formData) throws IOException {
        String template = loadHtmlTemplate("chatbot/email-template.html");
        Map<String, String> placeholders = createPlaceholderMap(formData);

        String htmlContent = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() != null ? entry.getValue() : "";
            htmlContent = htmlContent.replace("{" + entry.getKey() + "}", value);
        }
        return htmlContent;
    }
    
    private  Map<String, String> createPlaceholderMap(FormData formData) {
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
      


        // Zusätzliche Angaben
        placeholders.put("isHomeCare", Optional.ofNullable(formData.getHomeCare()).map(b -> b ? "Ja" : "Nein").orElse("Nein"));
        placeholders.put("careDurationMin6Months", Optional.ofNullable(formData.getCareDurationMin6Months()).map(b -> b ? "Ja" : "Nein").orElse("Nein"));
        placeholders.put("legalAcknowledgement", Optional.ofNullable(formData.getLegalAcknowledgement()).map(b -> b ? "Ja" : "Nein").orElse("Nein"));

        return placeholders;
    }

    private void sendEmail(String htmlContent) throws ResendException  {
        Resend resend = new Resend(resendApiKey);
        String receiver = "edh1579@thi.de";
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Pflegital <test@pflegital.de>")
                .to(receiver)
                .subject("Antrag auf Verhinderungspflege - Pflegital.de")
                .html(htmlContent)
                .build();
                CreateEmailResponse data = resend.emails().send(params);
                LOG.info("E-Mail an Pflegekraft erfolgreich versendet. ID: {}", data.getId());
    }

    private String loadHtmlTemplate(String fileName) throws IOException {
        try (InputStream inputStream = MailVersand.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) throw new IOException("HTML Template nicht gefunden: " + fileName);
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) content.append(line).append('\n');
            }
            return content.toString();
        }
    }
    
    private  String formatAddress(Address address) {
        if (address == null) return "";
        return String.format("%s %s, %s %s", 
            address.getStreet(), address.getHouseNumber(), address.getZip(), address.getCity()).trim();
    }
    
    private  String formatDate(LocalDate date) {
        if (date == null) return "";
        try {
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return date.toString();
        }
    }

  
}