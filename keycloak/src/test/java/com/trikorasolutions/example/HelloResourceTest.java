package com.trikorasolutions.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.is;

@QuarkusTest
public class HelloResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloResourceTest.class);

  private static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
  private static final String KEYCLOAK_REALM = "quarkus";

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  @Test
  public void testPublicResource() {
    RestAssured.given()
      .when().get("/hello/msg/Keycloak")
      .then()
      .statusCode(200)
      .body(is("Hello Keycloak"));
  }

}