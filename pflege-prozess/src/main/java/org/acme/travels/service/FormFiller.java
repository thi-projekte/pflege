package org.acme.travels.service;
 
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.travels.model.*;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
 
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
 
import static org.slf4j.LoggerFactory.getLogger;
 
@ApplicationScoped
public class FormFiller {
 
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Logger Log = getLogger(FormFiller.class);
 
    public void fillForm(FormData message) throws IOException {
        String outputPdfPath = prepareOutputPath();
        try (InputStream inputStream = loadTemplate();
             PDDocument pdfDocument = PDDocument.load(inputStream)) {
 
            PDAcroForm form = pdfDocument.getDocumentCatalog().getAcroForm();
            if (form == null) {
                Log.error("Kein AcroForm im PDF gefunden.");
                return;
            }
 
            fillFormFields(form, message);
            form.flatten();
            savePdf(pdfDocument, outputPdfPath);
 
        } catch (IOException e) {
            Log.error("Fehler beim Ausfüllen des PDFs: {}", e.getMessage());
        }
    }
 
    private String prepareOutputPath() throws IOException {
        String exportDir = ConfigProvider.getConfig().getValue("app.export.dir", String.class);
        Files.createDirectories(Paths.get(exportDir));
        return exportDir + File.separator + "ausgefuellter_antrag.pdf";
    }
 
    protected InputStream loadTemplate() throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf");
        if (inputStream == null) {
            throw new IOException("PDF nicht im Classpath gefunden: PflegeAntrag/de015_Antrag_Verhinderungspflege.pdf");
        }
        return inputStream;
    }
 
    private void savePdf(PDDocument pdfDocument, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            pdfDocument.save(fos);
            Log.info("PDF erfolgreich gespeichert unter: {}", outputPath);
        }
    }
 
    private void fillFormFields(PDAcroForm form, FormData message) throws IOException {
        Carerecipient cr = message.getCareRecipient();
        if (cr == null) return;
 
        fillCareRecipient(form, cr);
        fillCarePeriod(form, message);
        fillCaregiver(form, message);
        fillReason(form, message.getReason());
        fillReplacementCare(form, message);
        logFormFields(form);
    }
 
    private void fillCareRecipient(PDAcroForm form, Carerecipient cr) throws IOException {
        setField(form, "map_Versicherter_Fullname_Nachname_Vorname", cr.getFullName());
        setField(form, "map_Versicherter_Geburtsdatum", cr.getBirthDate() != null ? cr.getBirthDate().toString() : "");
        setField(form, "map_Versicherter_Telefon", cr.getPhoneNumber());
        setField(form, "map_Versicherter_VersNummer", "451424200695");
        setField(form, "map_System_Tagesdatum", LocalDate.now().format(DATE_FORMATTER));
 
        if (cr.getInsuredAddress() != null) {
            Address adr = cr.getInsuredAddress();
            setField(form, "map_Versicherter_Strasse_Hausnummer", adr.getStreet() + " " + adr.getHouseNumber());
            setField(form, "map_Versicherter_PLZ_Ort", adr.getZip() + " " + adr.getCity());
        }
    }
 
    private void fillCarePeriod(PDAcroForm form, FormData message) throws IOException {
        String start = message.getCarePeriod().getCareStart().format(DATE_FORMATTER);
        String end = message.getCarePeriod().getCareEnd().format(DATE_FORMATTER);
        setField(form, "Die Verhinderungspflege wird in folgendem Zeitraum durchgeführt: Datum vo", start);
        setField(form, "Die Verhinderungspflege wird in folgendem Zeitraum durchgeführt: Datum bis", end);
    }
 
    private void fillCaregiver(PDAcroForm form, FormData message) throws IOException {
        setField(form, "Name der Pflegeperson", message.getCaregiver().getCaregiverName());
        Address addr = message.getCaregiver().getCaregiverAddress();
        String fullAddress = addr.getZip() + " " + addr.getCity() + " " + addr.getStreet() + " " + addr.getHouseNumber();
        setField(form, "Anschrift der Pflegeperson", fullAddress);
        setField(form, "Telefonnummer der Pflegeperson", message.getCaregiver().getCaregiverPhoneNumber());
    }
 
    private void fillReason(PDAcroForm form, Reason reason) throws IOException {
    if (reason == null) {
        setField(form, "Die stunden- bzw. tageweise Verhinderungspflege wird erbracht:", "");
        return;
    }

    String text = switch (reason) {
        case URLAUB -> "weil meine Pflegeperson wegen Urlaub vorübergehend verhindert ist.";
        case SONSTIGES -> "weil meine Pflegeperson aus sonstigen Gründen vorübergehend verhindert ist";
        default -> "";
    };
    setField(form, "Die stunden- bzw. tageweise Verhinderungspflege wird erbracht:", text);
}

 
    private void fillReplacementCare(PDAcroForm form, FormData message) throws IOException {
        if (message.getReplacementCare().isProfessional()) {
            setField(form, "Die Verhinderungspflege wird durchgeführt von", "eine erwerbsmäßig tätige Pflegeperson");
            setField(form, "Name der Einrichtung / des Pflegedienstes",
                    message.getReplacementCareCareGiver().getRegularCaregiverName());
            setField(form, "Anschrift der Einrichtung / des Pflegedienstes", "Teststraße");
        } else {
            setField(form, "Die Verhinderungspflege wird durchgeführt von", "eine Privatperson");
        }
    }
 
    private void logFormFields(PDAcroForm form) {
        for (PDField field : form.getFields()) {
            Log.info("Feldname {}", field.getFullyQualifiedName());
        }
    }
 
    void setField(PDAcroForm form, String name, String value) throws IOException {
        PDField field = form.getField(name);
        if (field != null) {
            field.setValue(value != null ? value : "");
        } else {
            Log.error("PDF-Feld nicht gefunden: {}", name);
        }
    }
}