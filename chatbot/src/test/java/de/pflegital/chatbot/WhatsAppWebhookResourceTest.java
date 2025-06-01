package de.pflegital.chatbot;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestProfile(WebhookTestProfile.class)
@TestSecurity(authorizationEnabled = false)
class WhatsAppWebhookResourceTest {

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
}