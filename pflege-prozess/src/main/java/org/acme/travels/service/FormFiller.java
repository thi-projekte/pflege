package org.acme.travels.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.syncope.common.rest.api.service.BpmnProcessService;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.travels.model.FormData;
import org.acme.travels.model.Reason;
import org.acme.travels.model.Address;
import org.acme.travels.model.Carerecipient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;

import io.quarkus.logging.Log;

import static org.slf4j.LoggerFactory.getLogger;
import java.nio.file.Files;
import java.nio.file.Paths;

@ApplicationScoped
public class FormFiller {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Logger Log = getLogger(BpmnProcessService.class);
    public void fillForm(FormData message) throws IOException {
        String exportDir = ConfigProvider.getConfig().getValue("app.export.dir", String.class);
        Files.createDirectories(Paths.get(exportDir));
        String outputPdfPath = exportDir + File.separator + "ausgefuellter_antrag.pdf";

        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf")) {

            if (inputStream == null) {
                throw new IOException("PDF nicht im Classpath gefunden: PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf");
            }

            try (PDDocument pdfDocument = PDDocument.load(inputStream)) {
                PDAcroForm form = pdfDocument.getDocumentCatalog().getAcroForm();

                if (form != null) {
                    Carerecipient cr = message.getCareRecipient();
                    LocalDate careStart = message.getCarePeriod().getCareStart();
                    LocalDate careEnd = message.getCarePeriod().getCareEnd();

                    String formattedStart = careStart.format(DATE_FORMATTER);
                    String formattedEnd = careEnd.format(DATE_FORMATTER);

                    if (cr != null) {
                        setField(form, "map_Versicherter_Fullname_Nachname_Vorname", cr.getFullName());
                        setField(form, "map_Versicherter_Geburtsdatum",
                                cr.getBirthDate() != null ? cr.getBirthDate().toString() : "");
                        setField(form, "map_Versicherter_Telefon", cr.getPhoneNumber());
                        setField(form, "map_Versicherter_VersNummer", "451424200695");
                        setField(form, "map_System_Tagesdatum", LocalDate.now().format(DATE_FORMATTER));
                        setField(form, "Name der Pflegeperson", message.getCaregiver().getCaregiverName());
                        setField(form, "Anschrift der Pflegeperson",
                                message.getCaregiver().getCaregiverAddress().getZip()
                                        + message.getCaregiver().getCaregiverAddress().getCity()
                                        + message.getCaregiver().getCaregiverAddress().getStreet()
                                        + message.getCaregiver().getCaregiverAddress().getHouseNumber());
                        setField(form, "Telefonnummer der Pflegeperson",
                                message.getCaregiver().getCaregiverPhoneNumber());
                        setField(form, "Die Verhinderungspflege wird in folgendem Zeitraum durchgeführt: Datum vo",
                                formattedStart);
                        setField(form, "Die Verhinderungspflege wird in folgendem Zeitraum durchgeführt: Datum bis",
                                formattedEnd);

                        for (PDField field : form.getFields()) {
                            Log.info("Feldname " + field.getFullyQualifiedName());
                        }

                        if (cr.getInsuredAddress() != null) {
                            Address adr = cr.getInsuredAddress();
                            setField(form, "map_Versicherter_Strasse_Hausnummer",
                                    adr.getStreet() + " " + adr.getHouseNumber());
                            setField(form, "map_Versicherter_PLZ_Ort", adr.getZip() + " " + adr.getCity());
                        }

                     if (message.getReason() == Reason.URLAUB) {
    setField(form, "Die stunden- bzw. tageweise Verhinderungspflege wird erbracht:",
             "weil meine Pflegeperson wegen Urlaub vorübergehend verhindert ist.");
} else if (message.getReason() == Reason.SONSTIGES) {
    setField(form, "Die stunden- bzw. tageweise Verhinderungspflege wird erbracht:",
             "weil meine Pflegeperson aus sonstigen Gründen vorübergehend verhindert ist");
}

                        if (message.getReplacementCare().isProfessional()) {
                            setField(form, "Die Verhinderungspflege wird durchgeführt von",
                                    "eine erwerbsmäßig tätige Pflegeperson");
                            setField(form, "Name der Einrichtung / des Pflegedienstes",
                                    message.getReplacementCareCareGiver().getRegularCaregiverName());
                            setField(form, "Anschrift der Einrichtung / des Pflegedienstes", "Teststraße");
                        } else {
                            setField(form, "Die Verhinderungspflege wird durchgeführt von", "eine Privatperson");
                        }
                    }
                } else {
                    Log.error("Kein AcroForm im PDF gefunden.");
                }

                form.flatten();

                try (FileOutputStream fos = new FileOutputStream(new File(outputPdfPath))) {
                    pdfDocument.save(fos);
                    Log.info("PDF erfolgreich gespeichert unter: " + outputPdfPath);
                }

            }
        } catch (IOException e) {
            Log.error("Fehler beim Ausfüllen des PDFs: "+ e.getMessage());
        }
    }

    private void setField(PDAcroForm form, String name, String value) throws IOException {
        PDField field = form.getField(name);
        if (field != null) {
            field.setValue(value != null ? value : "");
        } else {
            Log.error("PDF-Feld nicht gefunden: " + name);
          
        }
    }
}
