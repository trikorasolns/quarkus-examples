package com.trikorasolutions.example;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import static javax.ws.rs.core.Response.Status.*;


@QuarkusTest
public class PolicyEnforcerTest {

    private static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
    private static final String KEYCLOAK_REALM = "quarkus";

    static {
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void testAccessUserResource() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/users/me")
                .then()
                .statusCode(OK.getStatusCode());
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/users/me")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    public void testAccessAdminResource() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/admin")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/admin")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
        RestAssured.given().auth().oauth2(getAccessToken("admin"))
                .when().get("/api/admin")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    public void testPublicResource() {
        RestAssured.given()
                .when().get("/api/public")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    private String getAccessToken(String userName) {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "backend-service")
                .param("client_secret", "secret")
                .when()
                .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }
}
