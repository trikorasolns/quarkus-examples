package com.trikorasolutions.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static javax.ws.rs.core.Response.Status.*;


@QuarkusTest
public class UserResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResourceTest.class);

  private static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
  private static final String KEYCLOAK_REALM = "quarkus";

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }


  @Test
  public void testRoles() {

    RestAssured.given().auth().oauth2(getAccessToken("alice")).when().contentType(MediaType.APPLICATION_JSON)
      .get("/api/users/roles").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
      .body("userRoles", containsInAnyOrder("user"));

    RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
      .get("/api/users/roles").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
      .body("userRoles", containsInAnyOrder("user", "confidential"));

  }

  @Test
  public void testInfo() {
    LOGGER.info("RESPONSE IN JSON FORMAT: {}",
      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON).extract().response()
        .jsonPath().prettyPrint());

      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
        .body("userName", is("jdoe"),
          "givenName",is("John"), // <==>"givenName", Matchers.containsString("John"),
          "familyName", is("Doe"),
          "email", is("johndoe@trikorasolutions.com"),
          "userRoles", Matchers.containsInAnyOrder("user","confidential"),
          "userRoles.size()",is(2),
          "userCredentials.type", Matchers.containsInAnyOrder("bearer"),
          "userCredentials.token[0]", Matchers.containsString("ey"),
          "userPermissions.rsname", Matchers.contains("User Resource"),
          "userPermissions[0].size()",is(2)
        );
  }

  private String getAccessToken(String userName) {
    return RestAssured.given().param("grant_type", "password").param("username", userName).param("password", userName)
      .param("client_id", "backend-service").param("client_secret", "secret").when()
      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
      .as(AccessTokenResponse.class).getToken();
  }
}