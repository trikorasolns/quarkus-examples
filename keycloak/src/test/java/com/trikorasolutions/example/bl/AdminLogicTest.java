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
  public void testUserCreateOk() {
    UserDto testUserDto = new UserDto("testUserCreateOk","testUserCreateOk","testUserCreateOk@trikorasolutions.com",
      true,"testUserCreateOk");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
      .body(
        "email", Matchers.containsString(testUserDto.email.toLowerCase())
      );

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;
  }

  @Test
  public void testUserCreateDuplicateErr() {
    UserDto testUserDto = new UserDto("testUserCreateErr","testUserCreateErr","testUserCreateErr@trikorasolutions.com",
      true,"testUserCreateErr");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().body(testUserDto).contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then().statusCode(OK.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().body(testUserDto).contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then().statusCode(CONFLICT.getStatusCode())
    ;
  }

  @Test
  public void testUserCreateUnauthorizedErr() {
    UserDto testUserDto = new UserDto("testUserCreateErr","testUserCreateErr","testUserCreateErr@trikorasolutions.com",
      true,"testUserCreateErr");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2("unknown")
      .when().body(testUserDto).contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then().statusCode(UNAUTHORIZED.getStatusCode())
    ;
  }

  @Test
  public void testUserCreateNoRealmErr() {
    UserDto testUserDto = new UserDto("testUserCreateErr","testUserCreateErr","testUserCreateErr@trikorasolutions.com",
      true,"testUserCreateErr");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().body(testUserDto).contentType("application/json")
      .post("/api/admin/unknown/users")
      .then().statusCode(NOT_FOUND.getStatusCode())
    ;
  }

  @Test
  public void testUserCreateMarshallingErr() {
    UserDto testUserDto = new UserDto("testUserCreateErr","testUserCreateErr","testUserCreateErr@trikorasolutions.com",
      true,"testUserCreateErr");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    testUserDto.userName = null;
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().body(testUserDto).contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then().statusCode(BAD_REQUEST.getStatusCode())
    ;
  }

  @Test
  public void testUserGetOk() {
    UserDto testUserDto = new UserDto("testUserGetOk","testUserGetOk","testUserGetOk@trikorasolutions.com",
      true,"testUserGetOk");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then().statusCode(OK.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .get(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
      .then()
      .statusCode(OK.getStatusCode())
      .body(
        "email", Matchers.containsString(testUserDto.email.toLowerCase())
      );

  }

  @Test
  public void testUserGetErr() {
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .get("/api/admin/trikorasolutions/users/unknown")
      .then()
      .statusCode(NOT_FOUND.getStatusCode())
    ;
  }

  @Test
  public void testUserUpdateOk() {
    UserDto testUserDto = new UserDto("testUserUpdateOk","testUserUpdateOk","testUserUpdateOk@trikorasolutions.com",
      true,"testUserUpdateOk");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
    ;

    testUserDto.setEmail("updated@trikorasolutions.com");
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .put("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
      .body(
        "email", Matchers.containsString(testUserDto.email.toLowerCase())
      );

  }

  @Test
  public void testUserUpdateErr() {
    UserDto testUserDto = new UserDto("testUserUpdateErr","testUserUpdateErr","testUserUpdateErr@trikorasolutions.com",
      true,"testUserUpdateErr");
    UserDto unknown = new UserDto("unknown","unknown","unknown@trikorasolutions.com",
      true,"unknown");

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
    ;

    // Keycloak usa los emails como PKs, do not repeat the email
    testUserDto.setEmail("updated2@trikorasolutions.com");
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .put("/api/admin/trikorasolutions/users")
      .then().statusCode(OK.getStatusCode())
    ;

    // Updated without modifying the info
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .put("/api/admin/trikorasolutions/users")
      .then().statusCode(OK.getStatusCode())
    ;

    // Update a user that is nor registered  in the db
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(unknown)
      .contentType("application/json")
      .put("/api/admin/trikorasolutions/users")
      .then().statusCode(NOT_FOUND.getStatusCode())
    ;

  }

  @Test
  public void testUserRemoveOk() {
    UserDto testUserDto = new UserDto("testUserRemoveOk","testUserRemoveOk","testUserRemoveOk@trikorasolutions.com",
      true,"testUserRemoveOk");
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName));

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .body(testUserDto)
      .contentType("application/json")
      .post("/api/admin/trikorasolutions/users")
      .then()
      .statusCode(OK.getStatusCode())
    ;

    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete(String.format("/api/admin/trikorasolutions/users/%s", testUserDto.userName))
      .then()
      .statusCode(OK.getStatusCode())
      .body(Matchers.containsString("true"))
    ;
  }

  @Test
  public void testUserRemoveErr() {
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when()
      .contentType("application/json")
      .delete("/api/admin/trikorasolutions/users/unknown")
      .then().statusCode(NOT_FOUND.getStatusCode())
      .body(Matchers.containsString("false"))
    ;
  }

  @Test
  public void testListKeycloakUsers() {
    RestAssured.given().auth().oauth2(getAccessToken("admin"))
      .when().get("/api/admin/listUsers/trikorasolutions")
      .then()
      .statusCode(OK.getStatusCode())
      .body("$.size()", Matchers.greaterThanOrEqualTo(4),
        "userName", Matchers.hasItems("jdoe","admin", "mrsquare", "mrtriangle")
      );
  }
}
