package org.acme.travels.service;

import org.acme.travels.model.*;
import org.acme.travels.model.replacementcare.ReplacementCareCareGiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MailVersandAnPflegekraftTest {

    private MailVersandAnPflegekraft mailService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        Path emailTemplatePath = tempDir.resolve("chatbot/email-template-Pflegekraft.html");
        File templateFile = emailTemplatePath.toFile();
        templateFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(templateFile)) {
            writer.write("""
                <html>
                    <body>
                        <p>Hallo {caregiverName},</p>
                        <p>Sie wurden für {careStart} bis {careEnd} eingetragen.</p>
                        <p>Versicherter: {insuredName}</p>
                    </body>
                </html>
                """);
        }

        System.setProperty("resend.api.key", "dummy");
        mailService = new MailVersandAnPflegekraft() {
            @Override
            protected String loadHtmlTemplate(String fileName) throws IOException {
                return Files.readString(emailTemplatePath);
            }

            @Override
            protected void sendEmail(String htmlContent, FormData formData) {
                assertNotNull(htmlContent);
                assertTrue(htmlContent.contains("Max Mustermann")); // Versichertendaten
            }
        };
    }

    @Test
    void testSendFormDataEmail_generatesCorrectHtmlAndSkipsNulls() {
        FormData data = buildFormData(true, true);
        assertDoesNotThrow(() -> mailService.sendFormDataEmail(data));
    }

    @Test
    void testSendFormDataEmail_handlesNullFieldsGracefully() {
        FormData data = buildFormData(false, false); // viele Felder auf null
        assertDoesNotThrow(() -> mailService.sendFormDataEmail(data));
    }

    @Test
    void testLoadHtmlTemplate_throwsIOExceptionIfNotFound() {
        MailVersandAnPflegekraft dummy = new MailVersandAnPflegekraft();
        assertThrows(IOException.class, () -> dummy.loadHtmlTemplate("nonexistent.html"));
    }

    private FormData buildFormData(boolean withCaregiver, boolean withReplacement) {
        FormData formData = new FormData();

        formData.setCareType(CareType.STUNDENWEISE);
        formData.setCareLevel(3);
        formData.setReason(Reason.URLAUB);
        formData.setCareDurationMin6Months(true);
        formData.setLegalAcknowledgement(false);
        formData.setHomeCare(true);

        formData.setCarePeriod(new Period(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10)));

        Carerecipient cr = new Carerecipient();
        cr.setFullName("Max Mustermann");
        cr.setBirthDate(LocalDate.of(1970, 1, 1));
        cr.setPhoneNumber("123456789");
        cr.setInsuranceNumber("ABC123");
        cr.setInsuredAddress(new Address("Straße", 1, "12345", "Stadt"));
        formData.setCareRecipient(cr);

        if (withCaregiver) {
            Caregiver caregiver = new Caregiver();
            caregiver.setCaregiverName("Erika Musterfrau");
            caregiver.setCaregiverPhoneNumber("987654321");
            caregiver.setCareStartDate(LocalDate.of(2024, 12, 1));
            caregiver.setCaregiverAddress(new Address("Pflegeweg", 10, "54321", "Pflegestadt"));
            formData.setCaregiver(caregiver);
        }

        if (withReplacement) {
            ReplacementCareCareGiver rcg = new ReplacementCareCareGiver();
            rcg.setRegularCaregiverName("Pflegedienst XY");
            rcg.setEmail("test@pflegedienst.de");
            formData.setReplacementCareCareGiver(rcg);
        }

        return formData;
    }
    @Test
void testLoadHtmlTemplate_realCall_throwsIfMissing() {
    MailVersandAnPflegekraft real = new MailVersandAnPflegekraft();
    assertThrows(IOException.class, () -> real.loadHtmlTemplate("nicht-da.html"));
}
@Test
protected void testCreatePlaceholderMap_booleanVarianten() {
    FormData data = new FormData();
    data.setCareDurationMin6Months(false);
    data.setLegalAcknowledgement(true);
    data.setHomeCare(false);
    data.setCareRecipient(null);
    data.setCaregiver(null);
    data.setReplacementCareCareGiver(null);

    Map<String, String> map = mailService.createPlaceholderMap(data);
    assertEquals("Nein", map.get("careDurationMin6Months"));
    assertEquals("Ja", map.get("legalAcknowledgement"));
    assertEquals("Nein", map.get("isHomeCare"));
}
@Test
 protected void testFormatDate_handlesNull() {
    MailVersandAnPflegekraft service = new MailVersandAnPflegekraft();
    String result = service.formatDate(null); // Methode ggf. auf protected ändern
    assertEquals("", result);
}

@Test
protected void testFormatAddress_nullAddressReturnsEmpty() {
    MailVersandAnPflegekraft service = new MailVersandAnPflegekraft();
    String result = service.formatAddress(null); // Methode ggf. auf protected ändern
    assertEquals("", result);
}


}
