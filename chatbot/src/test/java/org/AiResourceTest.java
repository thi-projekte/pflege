package org;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AiResourceTest {

    private static String sessionId;

    @BeforeAll
    public static void setUp() {
        sessionId = given()
                .header("Content-Type", "application/json")
                .when()
                .post("/chat/start")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("sessionId", notNullValue())
                .body("message", not(emptyOrNullString()))
                .extract().path("sessionId");
    }

    @Test
    public void testReplyChat() {

        // Sicherstellen, dass die sessionId aus dem Start-Test verwendet wird
        given()
                .header("Content-Type", "application/json")
                .queryParam("sessionId", sessionId)  // Verwende die sessionId aus dem Start-Chat
                .body("Mein Name ist Max Mustermann")  // Beispielhafte Benutzereingabe
                .when()
                .post("/chat/reply")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("sessionId", equalTo(sessionId))  // Prüft, ob die sessionId gleich ist
                .body("message", not(emptyOrNullString()))
                .body("formData", notNullValue());
    }
}