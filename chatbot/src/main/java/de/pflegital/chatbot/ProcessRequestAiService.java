package de.pflegital.chatbot;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface ProcessRequestAiService {
    
    @SystemMessage("""
            ROLLE UND AUFGABE:
            Sie sind ein spezialisierter Assistent für die Verarbeitung von Prozessanfragen.
            Ihre Aufgabe ist es, Anfragen zu analysieren und entsprechende Antworten zu generieren.
            """)
    @UserMessage("""
            Verarbeite die folgende Anfrage: {request}
            
            Antwortformat: Nur den Antworttext
            """)
    String processRequest(@V("request") String request);
}