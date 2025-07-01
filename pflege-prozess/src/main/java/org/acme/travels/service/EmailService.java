package org.acme.travels.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class EmailService {

 public void send(String empfaenger, String betreff, String inhalt) {
    final String username = "MS_7weksu@test-r6ke4n1zrxygon12.mlsender.net";
    final String password = "mssp.aRqwCDF.0r83ql3325plzw1j.LWwx1Ho";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.mailersend.net");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props,
        new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger));
        message.setSubject(betreff);

        // Teil 1: Text
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(inhalt);

        // Teil 2: Anhang (PDF-Datei)
        MimeBodyPart attachmentPart = new MimeBodyPart();

        //File pdfFile = new File("target/ausgefuellter_antrag.pdf"); // <- Pfad zur PDF-Datei
        String exportDir = ConfigProvider.getConfig().getValue("app.export.dir", String.class);
File    pdfFile = new File(exportDir, "ausgefuellter_antrag.pdf");
        attachmentPart.attachFile(pdfFile);

        // Kombiniere beides in einer MultiPart-Mail
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        // Nachrichtinhalt setzen
        message.setContent(multipart);

        Transport.send(message);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
}
