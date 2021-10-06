package com.trikorasolutions.example.bl;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

import static com.trikorasolutions.example.bl.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
public class UserLogicTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserLogicTest.class);

  @Test
  public void testUserInfo() {

//    LOGGER.info("TestUserInfo {}",
//      RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
//        .get("/api/users/kcuserinfo/trikorasolutions").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
//        .extract().response().prettyPrint());

    RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
      .get("/api/users/kcuserinfo/trikorasolutions").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
      .body("preferred_username.chars", Matchers.is("jdoe"),
        "given_name.chars", Matchers.is("John"),
        "family_name.chars", Matchers.is("Doe"),
        "email.chars", Matchers.is("johndoe@trikorasolutions.com")
      );
  }

}
