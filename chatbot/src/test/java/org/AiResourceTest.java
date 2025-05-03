package org;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestSecurity(authorizationEnabled = false) // Security in Tests deaktiviert
public class AiResourceTest {

    private String sessionId;

    private static final String SESSION_ID = "sessionId";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2_000;
    private static final Logger LOG = getLogger(AiResource.class);

    @BeforeEach
    public void setUp() {
        sessionId = retryRequest(() -> given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/chat/start")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(SESSION_ID, notNullValue())
                .extract()
                .path(SESSION_ID));
    }

    @Test
    public void testReplyChat() {
        String[] antworten = {
                "Max Mustermann",
                "01.04.2002",
                "12345678912",
                "Teststrasse 1 85053 Ingolstadt",
                "Tageweise",
                "Urlaub",
                "Pflegegrad 3",
                "Lukas Testmann",
                "01.02.2020",
                "Testsrtrasse 2 85053 Ingolstadt",
                "01.07.2025 - 30.07.2025",
                "Professionell",
                "Caritas",
                "Testsrtrasse 3 85053 Ingolstadt",
                "Es ist HomeCare",
                "Die Eingaben sind wahrheitgemäß"
        };

        int antwortIndex = 0;
        boolean completed = false;

        while (!completed && antwortIndex < antworten.length) {
            final String userInput = antworten[antwortIndex];

            Response response = retryRequest(() -> given()
                    .contentType(ContentType.JSON)
                    .queryParam(SESSION_ID, sessionId)
                    .body(userInput)
                    .when()
                    .post("/chat/reply")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(SESSION_ID, equalTo(sessionId))
                    .body("message", not(emptyOrNullString()))
                    .body("formData", notNullValue())
                    .extract()
                    .response());

            LOG.info("Empfangen Response für Input[{}]: {}", antwortIndex, response.asString());
            completed = Boolean.TRUE.equals(response.path("formData.completed"));
            if (!completed) {
                antwortIndex++;
            }
        }
    }

    // Fängt 500 Timeouts ab --> 3 retrys
    private static <T> T retryRequest(RequestSupplier<T> supplier) {
        Exception lastException = null;
        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                return supplier.get();
            } catch (AssertionError ae) {
                lastException = new RuntimeException(ae);
            } catch (Exception e) {
                lastException = e;
            }

            if (i < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted during retry", ie);
                }
            }
        }
        throw new IllegalStateException(
                "Request failed after " + MAX_RETRIES + " retries",
                lastException);
    }

    @FunctionalInterface
    private interface RequestSupplier<T> {
        T get();
    }
}
