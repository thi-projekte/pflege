package de.pflegital.chatbot;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.pflegital.chatbot.model.ChatResponse;
import de.pflegital.chatbot.services.WhatsAppRestClient;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestProfile(WebhookTestProfile.class)
@TestSecurity(authorizationEnabled = false)
class WhatsAppWebhookResourceTest {

    @InjectMock
    WhatsAppRestClient whatsAppClient;

    @InjectMock
    Pflegebot pflegebot;

    @BeforeEach
    void setUp() {
        // Set up mock behavior
        ChatResponse mockResponse = new ChatResponse();
        mockResponse.setMessage("This is a test response");

        Mockito.when(pflegebot.processUserInput(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockResponse);

        Mockito.doNothing().when(whatsAppClient).sendWhatsAppReply(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void testWebhookVerification() {
        // Simple challenge string
        String challenge = "test_challenge_123";

        // Test the webhook verification endpoint
        given()
                .queryParam("hub.mode", "subscribe")
                .queryParam("hub.verify_token", "winfprojekt")
                .queryParam("hub.challenge", challenge)
                .when()
                .get("/webhook")
                .then()
                .statusCode(200)
                .body(equalTo(challenge));
    }

    @Test
    void testInvalidVerificationToken() {
        // Test with invalid token
        given()
                .queryParam("hub.mode", "subscribe")
                .queryParam("hub.verify_token", "wrong_token")
                .queryParam("hub.challenge", "any_challenge")
                .when()
                .get("/webhook")
                .then()
                .statusCode(403);
    }

    @Test
    void testHandleWhatsAppMessage() {
        // Sample WhatsApp message payload
        String payload = "{\n" +
                "  \"object\": \"whatsapp_business_account\",\n" +
                "  \"entry\": [{\n" +
                "    \"id\": \"123456789\",\n" +
                "    \"changes\": [{\n" +
                "      \"field\": \"messages\",\n" +
                "      \"value\": {\n" +
                "        \"messages\": [{\n" +
                "          \"from\": \"491234567890\",\n" +
                "          \"type\": \"text\",\n" +
                "          \"text\": {\n" +
                "            \"body\": \"Hello, this is a test message\"\n" +
                "          }\n" +
                "        }]\n" +
                "      }\n" +
                "    }]\n" +
                "  }]\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/webhook")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo("EVENT_RECEIVED"));

        // Verify the bot was called to process the message
        Mockito.verify(pflegebot, Mockito.times(2)) // Called twice in your implementation
                .processUserInput(Mockito.eq("491234567890"), Mockito.eq("Hello, this is a test message"));

        // Verify a reply was sent
        Mockito.verify(whatsAppClient)
                .sendWhatsAppReply(Mockito.eq("491234567890"), Mockito.eq("This is a test response"));
    }

    @Test
    void testInvalidMessageFormat() {
        // Test with invalid payload
        String invalidPayload = "{ \"invalid\": \"format\" }";

        given()
                .contentType(ContentType.JSON)
                .body(invalidPayload)
                .when()
                .post("/webhook")
                .then()
                .statusCode(200) // Your implementation returns 200 even for invalid formats
                .contentType(ContentType.JSON)
                .body("status", equalTo("EVENT_RECEIVED"));

        // Verify no processing occurred
        Mockito.verify(pflegebot, Mockito.never())
                .processUserInput(Mockito.anyString(), Mockito.anyString());
    }
}
