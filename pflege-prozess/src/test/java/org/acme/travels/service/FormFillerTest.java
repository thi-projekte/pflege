package org.acme.travels.service;

import org.acme.travels.model.*;
import org.acme.travels.model.replacementcare.ReplacementCareCareGiver;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FormFillerTest {

    private FormFiller formFiller;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        formFiller = new FormFiller();
        System.setProperty("app.export.dir", tempDir.toString());
    }

    @Test
    void testFillForm_createsPdf() throws IOException {
        FormData formData = buildBaseFormData();
        formFiller.fillForm(formData);
        assertPdfExists();
    }

    @Test
    void testFillForm_withPrivateCare() throws IOException {
        FormData formData = buildBaseFormData();
        formData.getReplacementCare().setProfessional(false);
        formFiller.fillForm(formData);
        assertPdfExists();
    }

    @Test
    void testFillForm_withSonstigesReason() throws IOException {
        FormData formData = buildBaseFormData();
        formData.setReason(Reason.SONSTIGES);
        formFiller.fillForm(formData);
        assertPdfExists();
    }

    @Test
    void testFillForm_withUnknownReason() throws IOException {
        FormData formData = buildBaseFormData();
        formData.setReason(null); // default case im switch
        formFiller.fillForm(formData);
        assertPdfExists();
    }

    @Test
    void testFillForm_withoutCareRecipient() throws IOException {
        FormData formData = buildBaseFormData();
        formData.setCareRecipient(null); // überspringt fillFormFields()
        formFiller.fillForm(formData);
        assertPdfExists();
    }

    @Test
    void testFillForm_templateMissing_failsGracefully() {
        FormFiller brokenFiller = new FormFiller() {
            @Override
            protected InputStream loadTemplate() throws IOException {
                throw new IOException("Template fehlt!");
            }
        };

        FormData formData = buildBaseFormData();
        assertDoesNotThrow(() -> brokenFiller.fillForm(formData)); // Methode fängt Exception intern ab
    }

    @Test
    void testFillForm_noAcroForm_logsError() {
        FormFiller brokenFiller = new FormFiller() {
            @Override
            protected InputStream loadTemplate() {
                return new ByteArrayInputStream(new byte[0]); // leeres PDF ohne AcroForm
            }
        };

        FormData formData = buildBaseFormData();
        assertDoesNotThrow(() -> brokenFiller.fillForm(formData));
    }

   @Test
void testSetField_fieldNotFound_logsError() throws IOException {
    PDDocument doc = new PDDocument();
    PDDocumentCatalog catalog = new PDDocumentCatalog(doc);
    PDAcroForm form = new PDAcroForm(doc);
    catalog.setAcroForm(form);

    doc.getDocumentCatalog().setAcroForm(form);

    assertDoesNotThrow(() -> {
        formFiller.setField(form, "doesNotExist", "Testwert");
    });

    doc.close();
}


    // Hilfsmethode zum Validieren des PDF-Outputs
    private void assertPdfExists() {
        File output = tempDir.resolve("ausgefuellter_antrag.pdf").toFile();
        assertTrue(output.exists(), "PDF wurde nicht erstellt");
        assertTrue(output.length() > 0, "PDF-Datei ist leer");
    }

    // Standard-Testdaten erzeugen
    private FormData buildBaseFormData() {
        FormData formData = new FormData();

        formData.setCarePeriod(new Period(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10)));

        Carerecipient cr = new Carerecipient();
        cr.setFullName("Max Mustermann");
        cr.setBirthDate(LocalDate.of(1970, 1, 1));
        cr.setPhoneNumber("123456789");
        cr.setInsuredAddress(new Address("Musterstraße", 1, "12345", "Musterstadt"));
        formData.setCareRecipient(cr);

        Caregiver caregiver = new Caregiver();
        caregiver.setCaregiverName("Erika Musterfrau");
        caregiver.setCaregiverPhoneNumber("987654321");
        caregiver.setCaregiverAddress(new Address("Pflegeweg", 1, "54321", "Pflegestadt"));
        formData.setCaregiver(caregiver);

        formData.setReason(Reason.URLAUB);

        ReplacementCare rc = new ReplacementCare();
        rc.setProfessional(true);
        formData.setReplacementCare(rc);

        ReplacementCareCareGiver rccg = new ReplacementCareCareGiver();
        rccg.setRegularCaregiverName("Pflegedienst XY");
        formData.setReplacementCareCareGiver(rccg);

        return formData;
    }
}
