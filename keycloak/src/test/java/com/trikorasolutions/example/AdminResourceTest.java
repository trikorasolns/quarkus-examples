package com.trikorasolutions.example;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;

import static com.trikorasolutions.example.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.*;


@QuarkusTest
public class AdminResourceTest {

    static {
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void testAccessUserResource() {
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/users/me")
                .then()
                .statusCode(OK.getStatusCode());

        RestAssured.given().auth().oauth2(getAccessToken("admin"))
          .when().get("/api/users/me")
          .then()
          .statusCode(OK.getStatusCode());
    }

    @Test
    public void testAccessAdminResource() {
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/admin")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());

        RestAssured.given().auth().oauth2(getAccessToken("admin"))
                .when().get("/api/admin")
                .then()
                .statusCode(OK.getStatusCode());
    }

}
