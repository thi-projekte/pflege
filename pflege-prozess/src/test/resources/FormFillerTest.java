package org.acme.travels.service;

import org.acme.travels.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FormFillerTest {

    private FormFiller formFiller;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        formFiller = new FormFiller();

        // Setze systemweite Konfiguration für Exportpfad (sofern MicroProfile Config mockbar)
        System.setProperty("app.export.dir", tempDir.toString());
    }

    @Test
    void testFillForm_createsPdf() throws IOException {
        // Arrange – Testdaten bauen
        FormData formData = new FormData();
        formData.setCarePeriod(new CarePeriod(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 10)));

        Carerecipient cr = new Carerecipient();
        cr.setFullName("Max Mustermann");
        cr.setBirthDate(LocalDate.of(1970, 1, 1));
        cr.setPhoneNumber("123456789");
        Address insuredAddress = new Address("Musterstraße", "1", "12345", "Musterstadt");
        cr.setInsuredAddress(insuredAddress);
        formData.setCareRecipient(cr);

        Caregiver caregiver = new Caregiver();
        caregiver.setCaregiverName("Erika Musterfrau");
        caregiver.setCaregiverPhoneNumber("987654321");
        caregiver.setCaregiverAddress(new Address("Pflegeweg", "2", "54321", "Pflegestadt"));
        formData.setCaregiver(caregiver);

        formData.setReason(Reason.URLAUB);

        ReplacementCare rc = new ReplacementCare();
        rc.setProfessional(true);
        formData.setReplacementCare(rc);

        ReplacementCareCareGiver rccg = new ReplacementCareCareGiver();
        rccg.setRegularCaregiverName("Pflegedienst XY");
        formData.setReplacementCareCareGiver(rccg);

        // Act – Methode aufrufen
        formFiller.fillForm(formData);

        // Assert – Datei prüfen
        File output = tempDir.resolve("ausgefuellter_antrag.pdf").toFile();
        assertTrue(output.exists(), "PDF wurde nicht erstellt");
        assertTrue(output.length() > 0, "PDF-Datei ist leer");
    }
}
