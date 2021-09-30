package com.trikorasolutions.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.is;
import static com.trikorasolutions.example.KeycloakInfo.getAccessToken;

@QuarkusTest
public class PublicResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(PublicResourceTest.class);

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  @Test
  public void testPublicResource() {
    RestAssured.given()
      .when().get("/api/public/msg/Keycloak")
      .then()
      .statusCode(OK.getStatusCode())
      .body(is("Hello Keycloak"));
  }

  /**
   * TODO: verify public access with auth
   */
  @Test
  public void testPublicResourceWithAuth() {
    RestAssured.given().auth().oauth2(getAccessToken("mrtriangle"))
      .when().get("/api/public/msg/Keycloak")
      .then()
      .statusCode(FORBIDDEN.getStatusCode());

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().get("/api/public/msg/Keycloak")
      .then()
      .statusCode(FORBIDDEN.getStatusCode());
  }

}
