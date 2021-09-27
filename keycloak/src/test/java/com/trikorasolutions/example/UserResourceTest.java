package com.trikorasolutions.example;

import graphql.Assert;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.List;


@QuarkusTest
public class UserResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResourceTest.class);

  private static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
  private static final String KEYCLOAK_REALM = "quarkus";

  static {
    RestAssured.useRelaxedHTTPSValidation();
  }

  @Test
  public void testUserInfo() {
    LOGGER.info("eeeeeeeeeeeeeeeeeeeee{}",
      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(200).contentType(MediaType.APPLICATION_JSON).extract().response()
        .jsonPath().prettyPrint());

    MatcherAssert.assertThat(

      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(200).contentType(MediaType.APPLICATION_JSON).extract().response()
        .jsonPath().getList("attributes"),
  }

  //  @Test
  public void testRoles() {
//    try {
//      RestAssured.given().auth().oauth2(getAccessToken("trikora"));
//    } catch (IllegalArgumentException ex ) {
//      Assertions.assertTrue(ex.getMessage().contains("accessToken cannot be null"));
//    }

    List<String> responseList = RestAssured.given().auth().oauth2(getAccessToken("alice")).when()
      .contentType(MediaType.APPLICATION_JSON).get("/api/users/roles").then().statusCode(200)
      .contentType(MediaType.APPLICATION_JSON).extract().response().jsonPath().getList("$");

    Assert.assertTrue(2 == responseList.size());
    MatcherAssert.assertThat(responseList, Matchers.containsInAnyOrder("user", "confidential"));

//    RestAssured.given().auth().oauth2(getAccessToken("alice"))
//      .when().get("/api/users/userinfo")
//      .then()
//      .statusCode(200);

    List<String> responseList2 = RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when()
      .contentType(MediaType.APPLICATION_JSON).get("/api/users/roles").then().statusCode(200)
      .contentType(MediaType.APPLICATION_JSON).extract().response().jsonPath().getList("$");

    Assert.assertTrue(2 == responseList2.size());
    MatcherAssert.assertThat(responseList2, Matchers.containsInAnyOrder("user", "confidential"));


  }

  @Test
  public void testInfo() {
    LOGGER.info("eeeeeeeeeeeeeeeeeeeee{}",
      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(200).contentType(MediaType.APPLICATION_JSON).extract().response()
        .jsonPath().prettyPrint());

    MatcherAssert.assertThat(

      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
        .get("/api/users/userinfo").then().statusCode(200).contentType(MediaType.APPLICATION_JSON).extract().response()
        .jsonPath().getList("attributes"),

      Matchers.containsInAnyOrder("configuration-metadata", "tenant-id"));
  }

  private String getAccessToken(String userName) {
    return RestAssured.given().param("grant_type", "password").param("username", userName).param("password", userName)
      .param("client_id", "backend-service").param("client_secret", "secret").when()
      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
      .as(AccessTokenResponse.class).getToken();
  }
}