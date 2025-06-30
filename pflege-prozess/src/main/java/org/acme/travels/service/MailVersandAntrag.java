package org.acme.travels.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailVersandAntrag {

    @Inject
    EmailService emailService;

    public void executeWorkItem() {
       /*  String empfaenger = "denn.1998@web.de"; */
          String empfaenger = "edh1579@thi.de";
        String betreff = "Ausgefüllter Antrag auf Verhinderungspflege";
        String inhalt = "Bitte finden Sie den ausgefüllten Antrag im Anhang.";

        emailService.send(empfaenger, betreff, inhalt);
    }
}
