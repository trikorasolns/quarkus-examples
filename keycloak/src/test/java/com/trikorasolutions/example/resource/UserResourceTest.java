package com.trikorasolutions.example.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

import static com.trikorasolutions.example.resource.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.*;


@QuarkusTest
public class UserResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResourceTest.class);

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  @Test
  public void testRoles() {

    RestAssured.given().auth().oauth2(getAccessToken("mrsquare")).when().contentType(MediaType.APPLICATION_JSON)
     .get("/api/users/roles").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
      .body("userRoles", Matchers.hasItem("user"));

    RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
      .get("/api/users/roles").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
      .body("userRoles", Matchers.hasItems("user", "confidential"));
  }

  @Test
  public void testInfo() {
      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
        .body("userName", Matchers.is("jdoe"),
          "givenName", Matchers.is("John"),
          "familyName", Matchers.is("Doe"),
          "email", Matchers.is("johndoe@trikorasolutions.com"),
          "userRoles", Matchers.hasItems("user","confidential"),
          "userRoles.size()", Matchers.is(5),
          "userCredentials.type", Matchers.containsInAnyOrder("bearer"),
          "userCredentials.token[0]", Matchers.containsString("ey"),
          "userPermissions.rsname[0]", Matchers.matchesRegex("^[a-zA-Z0-9_ ]*$"),
          "userPermissions[0].size()", Matchers.is(2));
  }
}