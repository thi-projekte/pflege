package org.acme.travels.service;

import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private EmailService emailService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() throws Exception {
        File dummyPdf = new File(tempDir.toFile(), "ausgefuellter_antrag.pdf");
        try (FileWriter writer = new FileWriter(dummyPdf)) {
            writer.write("Fake PDF Inhalt");
        }

        emailService = new EmailService();
        emailService.username = "test";
        emailService.password = "test";
        emailService.host = "localhost";
        emailService.port = 2525;
        emailService.ssl = false;
        emailService.starttls = false;
        emailService.from = "noreply@dummy.de";
        emailService.exportDir = tempDir.toString();
    }

    @Test
    void testSend_emailTransportMocked_successful() {
        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            assertDoesNotThrow(() -> emailService.send("test@example.com", "Betreff", "Inhalt"));
            transportMock.verify(() -> Transport.send(any(MimeMessage.class)));
        }
    }
}
