package com.trikorasolutions.example.bl;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static com.trikorasolutions.example.bl.KeycloakInfo.getAccessToken;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
public class MapGroupAttrTest {
  /**
   * The purpose of this test is to figure out how to include the groups attributes in the user claims,
   * in order to map the attributes, you need to make a new mapper in the keycloak console:
   *
   * Firstly, go to the Clients in the navigator, then select the desired client and click on Mappers,
   * after that you need to create a new mapper (click on the create button). Once you are in the
   * "Create Protocol Mapper" section you have to select "User Attribute" in the "Mapper type" dropdown.
   *
   * Now, the "User Attribute" and "Token Claim name" form field should be displayed. The first one is the name
   * of the group attribute you want to link ("tkr-tenant" in our example) and the second one is the name
   * whose value will be use as the claim key ("tkr_tenant" in our example).
   *
   * take a look in the /api/users/tenant resource in the UserResource class to see how to access to the mapped
   * attribute from the user scope.
   */

  @Test
  public void testGetGroupAttrFromUser() {
    RestAssured.given().auth().oauth2(getAccessToken("jdoe")).when().contentType(MediaType.APPLICATION_JSON)
      .get("/api/users/tenant").then().statusCode(OK.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
      .body( Matchers.is("tenant1"));
  }

}

