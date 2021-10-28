package com.trikorasolutions.example.bl;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.trikorasolutions.example.bl.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class AdminLogicGroupTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminLogicGroupTest.class);

  @Test
  public void testGroupInfoOk() {
    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .get("/api/admin/trikorasolutions/groups/tenant-tenant1").then().statusCode(OK.getStatusCode())
      .body("id.chars", Matchers.is("a674d8a1-8a4d-42d5-976d-ba9c74d29433"),
        "name.chars", Matchers.is("tenant-tenant1")
      );

    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .get("/api/admin/trikorasolutions/groups/tenant-tenant1/listUsers").then().statusCode(OK.getStatusCode())
      .body("$.size()", Matchers.greaterThanOrEqualTo(1),
        "userName",Matchers.hasItem("jdoe")
      );
  }

  @Test
  public void testGroupInfoErr() {
    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .get("/api/admin/trikorasolutions/groups/unknown").then().statusCode(NOT_FOUND.getStatusCode());

  }

  @Test
  public void testPutUserInGroupOk() {
    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .put("/api/admin/trikorasolutions/users/mrsquare/groups/tenant-tenant1").then().statusCode(OK.getStatusCode())
      .body( "userName", is("mrsquare"));

    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .get("/api/admin/trikorasolutions/groups/tenant-tenant1/listUsers").then().statusCode(OK.getStatusCode())
      .body("$.size()", Matchers.greaterThanOrEqualTo(1),
        "userName",Matchers.hasItem("mrsquare")
      );

    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .delete("/api/admin/trikorasolutions/users/mrsquare/groups/tenant-tenant1").then().statusCode(OK.getStatusCode())
      .body( "userName", is("mrsquare"));

    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .get("/api/admin/trikorasolutions/groups/tenant-tenant1/listUsers").then().statusCode(OK.getStatusCode())
      .body("$.size()", Matchers.greaterThanOrEqualTo(1),
        "userName",(Matchers.not(Matchers.hasItem("mrsquare")))
      );
  }

  @Test
  public void testPutUserInGroupErr() {
    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .put("/api/admin/trikorasolutions/users/unknown/groups/tenant-tenant1").then().statusCode(BAD_REQUEST.getStatusCode());

    RestAssured.given().auth().oauth2(getAccessToken("admin")).when().contentType("application/json")
      .delete("/api/admin/trikorasolutions/users/unknown/groups/tenant-tenant1").then().statusCode(BAD_REQUEST.getStatusCode());
  }
}

