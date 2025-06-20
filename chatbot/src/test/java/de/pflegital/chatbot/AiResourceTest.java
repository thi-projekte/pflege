package de.pflegital.chatbot;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.pflegital.chatbot.api.AiResource;
import de.pflegital.chatbot.model.CareType;
import de.pflegital.chatbot.model.Carerecipient;
import de.pflegital.chatbot.services.AiService;
import de.pflegital.chatbot.tools.InsuranceNumberTool;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestSecurity(authorizationEnabled = false)
class AiResourceTest {

    @InjectMock
    AiService aiService;

    @InjectMock
    InsuranceNumberTool insuranceNumberTool;

    private String memoryId;
    private static final String SESSION_ID = "memoryId";
    private static final Logger LOG = getLogger(AiResource.class);

    @BeforeEach
    void setUp() {
        // Mock-FormData
        FormData mockFormData = new FormData();
        mockFormData.setCareLevel(3);
        mockFormData.setCareType(CareType.TAGEWEISE);
        Mockito.when(aiService.chatWithAiStructured(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockFormData);

        memoryId = given()
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
                .queryParam(SESSION_ID, memoryId)
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

    @Test
    void testCareLevelBelowTwo() {
        FormData mockFormData = new FormData();
        mockFormData.setCareLevel(1); // < 2
        Mockito.when(aiService.chatWithAiStructured(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockFormData);

        given()
                .contentType(ContentType.JSON)
                .queryParam(SESSION_ID, memoryId)
                .body("Pflegegrad 1")
                .when()
                .post("/chat/reply")
                .then()
                .statusCode(200)
                .body("formData.chatbotMessage", containsString("Pflegegrad 2"));
    }

    @Test
    void testInvalidInsuranceNumber() {
        FormData formData = new FormData();
        Carerecipient recipient = new Carerecipient();
        recipient.setInsuranceNumber("INVALID123");
        formData.setCareRecipient(recipient);
        formData.setCareLevel(3);
        formData.setChatbotMessage("Versicherungsnummer scheint ungültig zu sein"); // <- Hinzugefügt

        Mockito.when(aiService.chatWithAiStructured(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(formData);
        Mockito.when(insuranceNumberTool.isValidSecurityNumber("INVALID123")).thenReturn(false);

        given()
                .contentType(ContentType.JSON)
                .queryParam(SESSION_ID, memoryId)
                .body("Test mit ungültiger Versicherung")
                .when()
                .post("/chat/reply")
                .then()
                .statusCode(200)
                .body("formData.chatbotMessage", containsString("Versicherungsnummer scheint ungültig"));
    }

    @Test
    void testAiServiceThrowsException() {
        Mockito.when(aiService.chatWithAiStructured(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("AI failure"));

        given()
                .contentType(ContentType.JSON)
                .queryParam(SESSION_ID, memoryId)
                .body("Trigger Fehler")
                .when()
                .post("/chat/reply")
                .then()
                .statusCode(500); // WebApplicationException bei Fehler
    }

}
