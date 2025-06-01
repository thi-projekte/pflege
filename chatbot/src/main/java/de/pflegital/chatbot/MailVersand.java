package de.pflegital.chatbot;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;


public class MailVersand {
    public static void main(String[] args) {
        Resend resend = new Resend({$RESEND_API_KEY});

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <test@resend.dev>")
                .to("nip6168@thi.de")
                .subject("Antrag auf Verhinderungspflege")
                .html("<!DOCTYPE html>\r\n" + //
                                        "<html>\r\n" + //
                                        "  <head>\r\n" + //
                                        "    <style>\r\n" + //
                                        "      table {\r\n" + //
                                        "        font-family: Arial, sans-serif;\r\n" + //
                                        "        border-collapse: collapse;\r\n" + //
                                        "        width: 100%;\r\n" + //
                                        "        margin: 10px 0;\r\n" + //
                                        "      }\r\n" + //
                                        "      th, td {\r\n" + //
                                        "        border: 1px solid #dddddd;\r\n" + //
                                        "        text-align: left;\r\n" + //
                                        "        padding: 4px;\r\n" + //
                                        "        font-size: 14px;\r\n" + //
                                        "      }\r\n" + //
                                        "      th {\r\n" + //
                                        "        background-color: #f2f2f2;\r\n" + //
                                        "      }\r\n" + //
                                        "      h2 {\r\n" + //
                                        "        font-family: Arial, sans-serif;\r\n" + //
                                        "        font-size: 18px;\r\n" + //
                                        "        margin-bottom: 8px;\r\n" + //
                                        "      }\r\n" + //
                                        "    </style>\r\n" + //
                                        "  </head>\r\n" + //
                                        "  <body>\r\n" + //
                                        "    <h2>Pflegeantrag Übersicht</h2>\r\n" + //
                                        "    <table>\r\n" + //
                                        "      <tr><th colspan=\"2\">Allgemeine Informationen</th></tr>\r\n" + //
                                        "      <tr><td>Pflegeart</td><td>STUNDENWEISE</td></tr>\r\n" + //
                                        "      <tr><td>Pflegegrad</td><td>3</td></tr>\r\n" + //
                                        "      <tr><td>Pflegezeitraum</td><td>10.11.2025 – 21.12.2025</td></tr>\r\n" + //
                                        "      <tr><td>Grund</td><td>URLAUB</td></tr>\r\n" + //
                                        "      <tr><td>Pflege zu Hause</td><td>Ja</td></tr>\r\n" + //
                                        "      <tr><td>Pflege länger als 6 Monate</td><td>Ja</td></tr>\r\n" + //
                                        "      <tr><td>Rechtlicher Hinweis bestätigt</td><td>Ja</td></tr>\r\n" + //
                                        "    </table>\r\n" + //
                                        "\r\n" + //
                                        "    <table>\r\n" + //
                                        "      <tr><th colspan=\"2\">Versicherte Person</th></tr>\r\n" + //
                                        "      <tr><td>Name</td><td>Max Meier</td></tr>\r\n" + //
                                        "      <tr><td>Geburtsdatum</td><td>09.08.1970</td></tr>\r\n" + //
                                        "      <tr><td>Telefon</td><td>0157 32352131</td></tr>\r\n" + //
                                        "      <tr><td>Versichertennummer</td><td>985167234123</td></tr>\r\n" + //
                                        "      <tr><td>Adresse</td><td>Fraunhoferstraße 30, 80469 München</td></tr>\r\n" + //
                                        "    </table>\r\n" + //
                                        "\r\n" + //
                                        "    <table>\r\n" + //
                                        "      <tr><th colspan=\"2\">Pflegende Person</th></tr>\r\n" + //
                                        "      <tr><td>Name</td><td>Manuel Meier</td></tr>\r\n" + //
                                        "      <tr><td>Beginn der Pflege</td><td>10.11.2025</td></tr>\r\n" + //
                                        "      <tr><td>Telefon</td><td>0157 323512311</td></tr>\r\n" + //
                                        "      <tr><td>Adresse</td><td>Fraunhoferstraße 30, 80469 München</td></tr>\r\n" + //
                                        "    </table>\r\n" + //
                                        "\r\n" + //
                                        "    <table>\r\n" + //
                                        "      <tr><th colspan=\"2\">Ersatzpflege (Professionell)</th></tr>\r\n" + //
                                        "      <tr><td>Pflegeanbieter</td><td>Professional Pflege GmbH</td></tr>\r\n" + //
                                        "      <tr><td>Adresse</td><td>Esplaande 12, 80469 München</td></tr>\r\n" + //
                                        "    </table>\r\n" + //
                                        "  </body>\r\n" + //
                                        "</html>\r\n" + //
                                        "")
                .build();

         try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }
}