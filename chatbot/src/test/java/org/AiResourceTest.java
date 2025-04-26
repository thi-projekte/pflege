package org;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AiResourceTest {

    private static String token;
    private static String sessionId;

    private static final String SESSION_ID = "sessionId";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2_000; // 2 Sekunden

    @BeforeAll
    public static void setUp() {
        token = obtainAccessToken();

        sessionId = retryRequest(() ->
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType(ContentType.JSON)
                        .body("{}")
                        .when()
                        .post("http://localhost:8080/chat/start")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body(SESSION_ID, notNullValue())
                        .extract().path(SESSION_ID)
        );
    }

    private static String obtainAccessToken() {
        Config config = ConfigProvider.getConfig();
        String authServerUrl = config.getValue("quarkus.oidc.auth-server-url", String.class);
        String clientId       = config.getValue("quarkus.oidc.client-id", String.class);
        String clientSecret   = config.getValue("quarkus.oidc.credentials.secret", String.class);

        return retryRequest(() -> {
            Response resp = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("grant_type",    "client_credentials")
                    .formParam("client_id",     clientId)
                    .formParam("client_secret", clientSecret)
                    .when()
                    .post(authServerUrl + "/protocol/openid-connect/token");
            if (resp.statusCode() != 200) {
                throw new IllegalStateException("Token holen fehlgeschlagen: " + resp.statusCode());
            }
            return resp.jsonPath().getString("access_token");
        });
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
                "Die Eingabe sind wahrheitgemäß"

        };

        int antwortIndex = 0;
        boolean completed = false;

        while (!completed && antwortIndex < antworten.length) {
            final String userInput = antworten[antwortIndex];

            Response response = retryRequest(() ->
                    given()
                            .header("Authorization", "Bearer " + token)
                            .contentType(ContentType.JSON)
                            .queryParam(SESSION_ID, sessionId)
                            .body(userInput)
                            .when()
                            .post("http://localhost:8080/chat/reply")
                            .then()
                            .statusCode(200)
                            .contentType(ContentType.JSON)
                            .body(SESSION_ID, equalTo(sessionId))
                            .body("message", not(emptyOrNullString()))
                            .body("formData", notNullValue())
                            .extract().response()
            );

            completed = Boolean.TRUE.equals(response.path("formData.completed"));
            if (!completed) {
                antwortIndex++;
            }
        }
    }

    //Fängt 500 Timeouts ab --> 3 retrys
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
                lastException
        );
    }



    @FunctionalInterface
    private interface RequestSupplier<T> {
        T get();
    }
}
