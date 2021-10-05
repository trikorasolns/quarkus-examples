package com.trikorasolutions.example.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

import static com.trikorasolutions.example.bl.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.*;

@QuarkusTest
public class AdminResourceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminResourceTest.class);
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
