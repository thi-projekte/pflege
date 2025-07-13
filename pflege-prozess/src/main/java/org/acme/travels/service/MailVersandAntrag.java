package org.acme.travels.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MailVersandAntrag {

    
    EmailService emailService;

    public void executeWorkItem() {
        String empfaenger = "denn.1998@web.de";
        String betreff = "Ausgefüllter Antrag auf Verhinderungspflege";
        String inhalt = "Bitte finden Sie den ausgefüllten Antrag im Anhang.";

        emailService.send(empfaenger, betreff, inhalt);
    }
}
