package org.acme.travels.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

@ApplicationScoped // 🔥 Diese Zeile hinzufügen!
public class EmailService {

 public void send(String empfaenger, String betreff, String inhalt) {
    final String username = "MS_UBluNt@test-r9084zvdvx8gw63d.mlsender.net";
    final String password = "mssp.fKSU0mO.jpzkmgq0w324059v.iUWFj3S";

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
        File pdfFile = new File("target/ausgefuellter_antrag.pdf"); // <- Pfad zur PDF-Datei
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
