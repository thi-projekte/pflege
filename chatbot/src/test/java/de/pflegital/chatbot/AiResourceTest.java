package de.pflegital.chatbot;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import de.pflegital.chatbot.model.CareType;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
class AiResourceTest {

    @InjectMock
    AiService aiService;

    private String sessionId;
    private static final String SESSION_ID = "sessionId";
    private static final Logger LOG = getLogger(AiResource.class);

    @BeforeEach
    void setUp() {
        // Mock-FormData
        FormData mockFormData = new FormData();
        mockFormData.setCareLevel(3);
        mockFormData.setCareType(CareType.TAGEWEISE);
        Mockito.when(aiService.chatWithAiStructured(Mockito.anyString())).thenReturn(mockFormData);

        sessionId = given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/chat/start")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(SESSION_ID, notNullValue())
                .extract()
                .path(SESSION_ID);
    }

    @Test
    void testReplyChat() {

        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam(SESSION_ID, sessionId)
                .body("Pflegegrad und Art")
                .when()
                .post("/chat/reply")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("formData.careLevel", equalTo(3))
                .body("formData.careType", equalTo("TAGEWEISE"))
                .extract()
                .response();
        LOG.info("Response: {}", response.asString());
    }

    @Test
    void testReplyChatWithoutSessionId() {
        given()
                .contentType(ContentType.JSON)
                .body("Testeingabe ohne Session")
                .when()
                .post("/chat/reply")
                .then()
                .statusCode(401);
    }

}
