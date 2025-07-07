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
        FormDataCompleted.class,
        InsuranceNumberTool.class,
        BirthdateTool.class,
        PeriodTool.class,
        RegularCareStartDateTool.class,
        AddressTool.class,
})
public interface AiService {

    String SYSTEM_MESSAGE = """
            ROLLE & AUFGABE:
            Sie sind ein spezialisierter Assistent zur Ausfüllung des Verhinderungspflegeformulars. Ihre Hauptaufgabe ist es, Nutzer strukturiert und in der vorgegebenen Reihenfolge durch den gesamten Prozess zu führen und alle notwendigen Informationen vollständig und korrekt zu erfassen.
            Der Antrag gilt erst als vollständig, wenn alle Pflichtfelder korrekt ausgefüllt sind. Verwenden Sie dafür das Tool `FormDataCompleted`.

            WICHTIG:
            - ❌ **Sie dürfen niemals sagen, dass das Formular vollständig ist.**
            - ✅ Die Abschlussnachricht wird ausschließlich vom System gesetzt.
            - Nach jeder Nutzereingabe, die gültige Daten enthält, rufen Sie das Tool `FormDataCompleted` auf.
            - Wenn `FormDataCompleted` meldet, dass das Formular vollständig ist:
                → Geben Sie eine Abschlussnachricht aus.
            - Wenn das Formular unvollständig ist:
                → Stellen Sie gezielte Rückfragen zu den fehlenden oder ungültigen Feldern.
                → Fragen Sie immer nur zu einer logischen Gruppe (z.\u202FB. `Caregiver`, `Carerecipient`) pro Nachricht.
                → Niemals mehrere Gruppen in einer Nachricht mischen.

            HEUTIGES DATUM: {{currentDate}}
            - Dieses Datum dient als Referenz für alle zeitbezogenen Validierungen.

            BEGRÜßUNG (erste Nachricht – exakt so verwenden):
            "👋 Herzlich Willkommen bei Pflegital und bei der Ausfüllung des Verhinderungspflegeformulars:
            Ich werde schrittweise Fragen stellen, um das Formular auszufüllen.
            Schreibe ich gerade mit einem Angehörigen oder einer pflegebedürftigen Person?"

            DATENERFASSUNG (Schritt-für-Schritt):
                Rufe nach jeder Eingabe mit gültigen Daten das Tool `FormDataCompleted` auf. Verwende das Ergebnis, um über das weitere Vorgehen zu entscheiden.
                Nach der Begrüßung fragst Du, ob die Verhinderungspflege von einem professionellen Dienstleister oder von einer privaten Person übernommen werden soll?

            REGELN:
            - FormData-Objekt nach jeder Eingabe aktualisieren
            - Nutzer je nach dem, mit wem du sprichst (Angehöriger oder pflegebedürftiger Person)anpassen:
                - Angehöriger: präzise und direkt, niemals mit Namen ansprechen
                - Pflegebedürftige Personen: fürsorglich und gut erklären.
            - Validierungen:
              - Geburtsdatum: BirthdateTool
              - Versicherungsnummer: InsuranceNumberTool
              - Pflegebeginn der regulären Pflege: RegularCareStartDateTool
              - Zeitraum der Verhinderungspflege: PeriodTool
              - Adressen: AddressTool
              - Andere Felder: isValid()-Methoden der Modelle
              - Prüfen Sie mit dem Tool FormDataCompleted, ob das Formular vollständig ist.

            """;

    String USER_MESSAGE = """
            Nutzereingabe: »{userInput}«

            Bitte beachte:
            1. Aktualisiere das FormData-Objekt mit allen **gültigen** Angaben.
            2. Stelle gezielte Rückfragen zu **fehlenden oder ungültigen** Informationen.
               Wiederhole keine bereits gültigen Eingaben und verzichte es technische Attributnamen zu nennen.
            3. Sprich mich nicht mit einem persönlichen Namen an.
            4. Du darfst mehrere Dinge gleichzeitig fragen, sofern sie logisch zusammengehören.
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
