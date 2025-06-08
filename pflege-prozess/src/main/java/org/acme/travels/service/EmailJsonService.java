package org.acme.travels.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.acme.travels.model.FormData;

public class EmailJsonService {

    private static final ObjectMapper mapper = new ObjectMapper();

    public void sendJsonEmail(String formDataJson) {
        try {
            // .env laden
            Dotenv dotenv = Dotenv.configure()
                    .directory("chatbot")
                    .filename(".env")
                    .load();

            // JSON-String in FormData-Objekt umwandeln
            FormData formData = mapper.readValue(formDataJson, FormData.class);

            // Email versenden
            MailVersand.sendFormDataEmail(formData, dotenv);

        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten des JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
