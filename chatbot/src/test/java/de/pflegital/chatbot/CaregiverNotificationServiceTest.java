package de.pflegital.chatbot;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class CaregiverNotificationServiceTest {

    @InjectMock
    AiService aiService;

    @InjectMock
    WhatsAppRestClient whatsAppClient;

    @InjectMock
    CaregiverNotificationService notificationService;

    @BeforeEach
    void setUp() {
        // Mock AI Service response
        Mockito.when(aiService.generateCaregiverAssignmentMessage(Mockito.anyString()))
                .thenReturn("Sehr geehrte Frau Mustermann,\n\nwir freuen uns, Ihnen mitteilen zu können, dass Sie als Pflegekraft für Herrn Schmidt zugewiesen wurden.\n\nPflegezeitraum: 01.01.2024 - 15.01.2024\n\nBei Rückfragen erreichen Sie uns unter: 0800-123456\n\nMit freundlichen Grüßen\nIhr Pflegital-Team");
    }

    @Test
    void testNotifyCaregiver() {
        // Test data
        String caregiverName = "Maria Mustermann";
        String caregiverPhone = "491234567890";
        String careRecipientName = "Max Schmidt";
        String carePeriod = "01.01.2024 - 15.01.2024";

        // Execute test
        notificationService.notifyCaregiver(caregiverName, caregiverPhone, careRecipientName, carePeriod);

        // Verify AI service was called
        Mockito.verify(aiService).generateCaregiverAssignmentMessage(caregiverName);

        // Verify WhatsApp client was called
        Mockito.verify(whatsAppClient).sendWhatsAppReply(
            Mockito.eq(caregiverPhone),
            Mockito.anyString()
        );
    }

    @Test
    void testNotifyCaregiverWithError() {
        // Mock WhatsApp client to throw exception
        Mockito.doThrow(new RuntimeException("WhatsApp API error"))
                .when(whatsAppClient)
                .sendWhatsAppReply(Mockito.anyString(), Mockito.anyString());

        // Test data
        String caregiverName = "Maria Mustermann";
        String caregiverPhone = "491234567890";
        String careRecipientName = "Max Schmidt";
        String carePeriod = "01.01.2024 - 15.01.2024";

        // Execute test and verify exception is thrown
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> notificationService.notifyCaregiver(caregiverName, caregiverPhone, careRecipientName, carePeriod)
        );
    }
} 