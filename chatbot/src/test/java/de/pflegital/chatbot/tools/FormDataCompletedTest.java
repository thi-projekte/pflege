package de.pflegital.chatbot.tools;

import de.pflegital.chatbot.FormData;
import de.pflegital.chatbot.model.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FormDataCompletedTest {
    @Inject
    FormDataCompleted formDataCompleted;

    private FormData createValidFormData() {
        FormData formData = new FormData();
        formData.setCareLevel(3);
        formData.setCareType(CareType.STUNDENWEISE);
        formData.setCarePeriod(createValidPeriod());
        formData.setReason(Reason.URLAUB);
        formData.setCareRecipient(createValidCareRecipient());
        formData.setCaregiver(createValidCaregiver());
        formData.setReplacementCare(createValidReplacementCare());
        return formData;
    }

    private Period createValidPeriod() {
        Period period = new Period();
        period.setCareStart(LocalDate.now().plusDays(1));
        period.setCareEnd(LocalDate.now().plusDays(10));
        return period;
    }

    private Carerecipient createValidCareRecipient() {
        Carerecipient c = new Carerecipient();
        c.setFullName("Max Mustermann");
        c.setBirthDate(LocalDate.now().minusYears(50));
        c.setInsuranceNumber("12345678901");
        Address a = new Address();
        a.setStreet("Testweg");
        a.setHouseNumber(1);
        a.setZip("12345");
        a.setCity("Teststadt");
        c.setInsuredAddress(a);
        c.setPhoneNumber("+49123456789");
        return c;
    }

    private Caregiver createValidCaregiver() {
        Caregiver cg = new Caregiver();
        cg.setRegularCaregiverName("Erika Pflege");
        cg.setRegularCareStartedDate(LocalDate.now().minusMonths(7));
        Address a = new Address();
        a.setStreet("Pflegeweg");
        a.setHouseNumber(2);
        a.setZip("54321");
        a.setCity("Pflegestadt");
        cg.setRegularCaregiverAddress(a);
        cg.setRegularCaregiverPhoneNumber("0123456789");
        return cg;
    }

    private ReplacementCare createValidReplacementCare() {
        ReplacementCare rc = new ReplacementCare();
        rc.setIsProfessional(true);
        return rc;
    }

    @Test
    void testAllFieldsValid() {
        FormData formData = createValidFormData();
        String result = formDataCompleted.checkFormData(formData);
        assertEquals("Alle erforderlichen Felder sind ausgefüllt und gültig!", result);
    }

    @Test
    void testMissingReplacementCare() {
        FormData formData = createValidFormData();
        formData.setReplacementCare(null);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("Verhinderungspflege"));
    }

    @Test
    void testInvalidCareLevel() {
        FormData formData = createValidFormData();
        formData.setCareLevel(1);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("Pflegegrad"));
    }

    @Test
    void testMissingCareType() {
        FormData formData = createValidFormData();
        formData.setCareType(null);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("Art der Verhinderunsflege"));
    }

    @Test
    void testInvalidPeriod() {
        FormData formData = createValidFormData();
        Period invalid = new Period();
        invalid.setCareStart(LocalDate.now().minusDays(1)); // Vergangenheit
        invalid.setCareEnd(LocalDate.now().plusDays(10));
        formData.setCarePeriod(invalid);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("Zeitraum der Verhinderungspflege"));
    }

    @Test
    void testMissingReason() {
        FormData formData = createValidFormData();
        formData.setReason(null);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("Grund der Pflege"));
    }

    @Test
    void testInvalidCareRecipient() {
        FormData formData = createValidFormData();
        Carerecipient c = createValidCareRecipient();
        c.setFullName("");
        formData.setCareRecipient(c);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("pflegebedürftigen Person"));
    }

    @Test
    void testInvalidCaregiver() {
        FormData formData = createValidFormData();
        Caregiver cg = createValidCaregiver();
        cg.setRegularCaregiverName("");
        formData.setCaregiver(cg);
        String result = formDataCompleted.checkFormData(formData);
        assertTrue(result.contains("regulären Pflegekraft"));
    }
}
