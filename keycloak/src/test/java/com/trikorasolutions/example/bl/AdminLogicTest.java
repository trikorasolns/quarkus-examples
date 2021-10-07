package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.trikorasolutions.example.bl.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.*;

@QuarkusTest
public class AdminLogicTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminLogicTest.class);

  @Test
  public void testListKeycloakUsers() {
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().get("/api/admin/listUsers/trikorasolutions")
      .then()
      .statusCode(OK.getStatusCode())
      .body("$.size().toString()", Matchers.greaterThanOrEqualTo("4"),
        "username.chars", Matchers.hasItems("jdoe","admin", "mrsquare", "mrtriangle")
      );
  }

  @Test
  public void testCRUDUsers() {

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .get("/api/admin/trikorasolutions/users/unknown")
      .then()
      .statusCode(NOT_FOUND.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(new UserDto("mr","rectangle","mrrectangule@trikorasolutions.com",
        true,"mrrectangule"))
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
      .body(
        "email", Matchers.containsString("mrrectangule@trikorasolutions.com")
      );

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(new UserDto("mr","rectangle","mrrectangule@trikorasolutions.com",
        true,"mrrectangule"))
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(CONFLICT.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(new UserDto("mr","rectangle","updatedmail@trikorasolutions.com",
        true,"mrrectangule"))
      .contentType("application/json")
      .put("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(new UserDto("mr","rectangle","updatedmail@trikorasolutions.com",
        true,"mrrectangule"))
      .contentType("application/json")
      .put("/api/admin/trikorasolutions/unknown")
      .then()
      .statusCode(NOT_FOUND.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .get("/api/admin/trikorasolutions/users/mrrectangule")
      .then()
      .statusCode(OK.getStatusCode())
      .body(
        "email", Matchers.containsString("updatedmail@trikorasolutions.com")
      );

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete("/api/admin/trikorasolutions/users/mrrectangule")
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete("/api/admin/trikorasolutions/users/mrrectangule")
    ;

  }
}
