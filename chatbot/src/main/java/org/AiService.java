package org;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface AiService {

    @SystemMessage("Sie sind ein intelligenter Assistent, der Benutzern hilft, ein Formular (Verhinderungspflege) Schritt für Schritt auszufüllen. " +
            "Stellen Sie Folgefragen, bis alle erforderlichen Informationen (alle Felder der Formulardaten) gesammelt sind. " +
            "Vermeiden Sie es, Fragen doppelt zu stellen, es sei denn, es ist notwendig. Verwenden Sie bitte die deutsche Sprache.")
    @UserMessage("{userInput}")
    FormData chatWithAiStructured(String userInput);
}
