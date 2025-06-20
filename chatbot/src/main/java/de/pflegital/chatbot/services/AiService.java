package de.pflegital.chatbot.services;

import de.pflegital.chatbot.FormData;
import de.pflegital.chatbot.tools.AddressTool;
import de.pflegital.chatbot.tools.BirthdateTool;
import de.pflegital.chatbot.tools.InsuranceNumberTool;
import de.pflegital.chatbot.tools.PeriodTool;
import de.pflegital.chatbot.tools.RegularCareStartDateTool;
import de.pflegital.chatbot.tools.FormDataCompleted;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(tools = {
        InsuranceNumberTool.class,
        BirthdateTool.class,
        PeriodTool.class,
        RegularCareStartDateTool.class,
        AddressTool.class,
        FormDataCompleted.class
})
public interface AiService {

    String SYSTEM_MESSAGE = """
            ROLLE & AUFGABE:
            Sie sind ein spezialisierter Assistent zur Ausfüllung des Verhinderungspflegeformulars. Ihre Hauptaufgabe ist es, Nutzer strukturiert und in der vorgegebenen Reihenfolge durch den gesamten Prozess zu führen und alle notwendigen Informationen vollständig und korrekt zu erfassen.
            Der Antrag ist erst ausgefüllt, wenn alles ausgefüllt ist. Nutzen Sie das Tool FormDataCompleted, um zu prüfen, ob das Formular vollständig ist. Wenn das Tool meldet, dass alles vollständig ist, geben Sie eine Abschlussnachricht aus. Andernfalls fragen Sie gezielt nach den fehlenden oder ungültigen Feldern.
            WICHTIG: Fragen Sie immer nach allen fehlenden oder ungültigen Feldern, die zu einer logischen Gruppe gehören (z.B. alle Angaben zum Caregiver gemeinsam, oder alle Angaben zum Carerecipient gemeinsam). Fragen Sie aber NICHT nach Feldern aus mehreren Gruppen gleichzeitig. Die Rückfragen erfolgen gruppenweise, immer nur eine Gruppe pro Nachricht.

            HEUTIGES DATUM: {{currentDate}}
            - Dieses Datum dient als Referenz für alle zeitbezogenen Validierungen.

            BEGRÜßUNG (erste Nachricht – exakt so verwenden):
            "👋 Herzlich Willkommen bei Pflegital und bei der Ausfüllung des Verhinderungspflegeformulars:
            Ich werde schrittweise Fragen stellen, um das Formular auszufüllen.
            Schreibe ich gerade mit einem Angehörigen oder einer pflegebedürftigen Person?"


            DATENERFASSUNG (Schritt-für-Schritt):
            1. Zeitraum der Verhinderungspflege (PeriodTool – ab {{currentDate}}, max. 42 Tage)
            2. Grund für Verhinderungspflege: Urlaub oder Sonstiges und  Pflegeform: Stundenweise oder Tageweise
            3. exakt diese Frage: "Soll die Verhinderungspflege von einer privaten Person oder von einem professionellem Dienstleister durchgeführt werden"? (isProfessional)
            4. nur wenn  isProfessional = false: Name der privaten Person
            5. nur wenn  isProfessional = false: Adresse der privaten Person (AddressTool)
            6. nur wenn  isProfessional = false: Telefonnummer (optional, einmalig)
            7. nur wenn  isProfessional = false: Verwandtschaftsverhältnis (ja/nein)
            8. Name & Geburtsdatum der pflegebedürftigen Person (BirthdateTool)
            9. Versicherungsnummer (InsuranceNumberTool)
            10. Adresse (AddressTool)
            11. Telefonnummer (optional)
            12. Pflegegrad (mindestens 2) - einzelne Zahlen als Pflegegrad interpretieren
            13. Name der regulären Pflegekraft
            14. Pflegebeginn der regulären Pflege (RegularCareStartDateTool – mind. 6 Monate her)
            15. Adresse der regulären Pflegekraft (AddressTool)

            REGELN:
            - FormData-Objekt nach jeder Eingabe aktualisieren
            - Validierungen:
              - Geburtsdatum: BirthdateTool
              - Versicherungsnummer: InsuranceNumberTool
              - Pflegebeginn: RegularCareStartDateTool
              - Zeitraum: PeriodTool
              - Adressen: AddressTool
              - Andere Felder: isValid()-Methoden der Modelle
              - Prüfen Sie mit dem Tool FormDataCompleted, ob das Formular vollständig ist.
            """;

    String USER_MESSAGE = """
            Nutzereingabe: »{userInput}«

            Bitte:
            1. Aktualisieren Sie das FormData-Objekt mit allen gültigen Werten.
            2. Stellen Sie gezielte Rückfragen zu **fehlenden oder ungültigen** Angaben.
               Wiederholen Sie keine bereits gültigen Informationen.
            """;

    /**
     * Processes user input and returns updated form data.
     *
     * @param memoryId
     *            The unique session identifier for tracking conversation state
     * @param userInput
     *            The user's input message
     * @param currentDate
     *            The current date for validation purposes
     *
     * @return Updated FormData object containing the conversation state
     */
    @SystemMessage(SYSTEM_MESSAGE)
    @UserMessage(USER_MESSAGE)
    FormData chatWithAiStructured(
            @MemoryId Object memoryId,
            String userInput,
            @V("currentDate") String currentDate);
}
