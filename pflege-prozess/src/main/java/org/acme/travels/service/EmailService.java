package org.acme.travels.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.util.Properties;

@ApplicationScoped
public class EmailService {

    @ConfigProperty(name = "kogito.services.email.username")
    String username;

    @ConfigProperty(name = "kogito.services.email.password")
    String password;

    @ConfigProperty(name = "kogito.services.email.host")
    String host;

    @ConfigProperty(name = "kogito.services.email.port")
    int port;

    @ConfigProperty(name = "kogito.services.email.ssl")
    boolean ssl;

    @ConfigProperty(name = "kogito.services.email.starttls")
    boolean starttls;

    @ConfigProperty(name = "kogito.services.email.from")
    String from;

    @ConfigProperty(name = "app.export.dir")
    String exportDir;

    public void send(String empfaenger, String betreff, String inhalt) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
        }

        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger));
            message.setSubject(betreff);

            // Teil 1: Text
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(inhalt);

            // Teil 2: Anhang
            MimeBodyPart attachmentPart = new MimeBodyPart();
            File pdfFile = new File(exportDir, "ausgefuellter_antrag.pdf");
            attachmentPart.attachFile(pdfFile);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Senden der E-Mail", e);
        }
    }
}
