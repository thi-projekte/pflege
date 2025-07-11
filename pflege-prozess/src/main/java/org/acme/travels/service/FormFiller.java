package org.acme.travels.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.travels.model.FormData;
import org.acme.travels.model.Address;
import org.acme.travels.model.Carerecipient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.microprofile.config.ConfigProvider;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


@ApplicationScoped
public class FormFiller {

    public void fillForm(FormData message) throws IOException {
       
       // String outputPdfPath = "target/ausgefuellter_antrag.pdf";

    String exportDir = ConfigProvider.getConfig().getValue("app.export.dir", String.class);
    Files.createDirectories(Paths.get(exportDir)); // falls das Verzeichnis noch nicht existiert
    String outputPdfPath = exportDir + File.separator + "ausgefuellter_antrag.pdf";

        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf")) {

            if (inputStream == null) {
                throw new IOException(
                        "PDF nicht im Classpath gefunden: PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf");
            }

            try (PDDocument pdfDocument = PDDocument.load(inputStream)) {
                PDAcroForm form = pdfDocument.getDocumentCatalog().getAcroForm();

                if (form != null) {
                    Carerecipient cr = message.getCareRecipient();
                    LocalDate careStart = message.getCarePeriod().getCareStart();
                    LocalDate careEnd = message.getCarePeriod().getCareEnd();
                    // Formatieren als z. B. "23.06.2025"
                    String formattedStart = careStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    String formattedEnd = careEnd.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                    if (cr != null) {
                        setField(form, "map_Versicherter_Fullname_Nachname_Vorname", cr.getFullName());
                        setField(form, "map_Versicherter_Geburtsdatum",
                                cr.getBirthDate() != null ? cr.getBirthDate().toString() : "");
                        setField(form, "map_Versicherter_Telefon", cr.getPhoneNumber());
                        setField(form, "map_Versicherter_VersNummer", "451424200695");
                        setField(form, "map_System_Tagesdatum",
                                LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
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
    System.out.println("Feldname: " + field.getFullyQualifiedName());
}


                        if (cr.getInsuredAddress() != null) {
                            Address adr = cr.getInsuredAddress();
                            setField(form, "map_Versicherter_Strasse_Hausnummer",
                                    adr.getStreet() + " " + adr.getHouseNumber());
                            setField(form, "map_Versicherter_PLZ_Ort", adr.getZip() + " " + adr.getCity());
                        }

                        if (message.getReason() != null) {
                            switch (message.getReason()) {
                                case URLAUB:
                                    setField(form, "Die stunden- bzw. tageweise Verhinderungspflege wird erbracht:",
                                            "weil meine Pflegeperson wegen Urlaub vorübergehend verhindert ist.");
                                    break;
                                case SONSTIGES:
                                    setField(form, "Die stunden- bzw. tageweise Verhinderungspflege wird erbracht:",
                                            "weil meine Pflegeperson aus sonstigen Gründen vorübergehend verhindert ist");
                                    break;
                            }
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

                }

                form.flatten(); // Formular sperren

                try (FileOutputStream fos = new FileOutputStream(new File(outputPdfPath))) {
                    pdfDocument.save(fos);
                    System.out.println("PDF erfolgreich gespeichert unter: " + outputPdfPath);
                }
                {
                    System.err.println("Kein AcroForm im PDF gefunden.");
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Ausfüllen des PDFs: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void setField(PDAcroForm form, String name, String value) throws IOException {
        PDField field = form.getField(name);
        if (field != null) {
            field.setValue(value != null ? value : "");
        } else {
            System.err.println("PDF-Feld nicht gefunden: " + name);
        }
    }

}
