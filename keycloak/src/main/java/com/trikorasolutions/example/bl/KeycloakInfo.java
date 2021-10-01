package com.trikorasolutions.example.bl;

public class KeycloakInfo {
  protected static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
  protected static final String KEYCLOAK_REALM = "trikorasolutions";
  protected static final String KEYCLOAK_CLIENT_SECRET = "6e521ebe-e300-450f-811a-a08adc42ec4a";
  protected static final String KEYCLOAK_CLIENT_ID = "backend-service";

//  public static String getAccessToken(String userName) {
//    return RestAssured.given()
//      .param("grant_type", "password")
//      .param("username", userName)
//      .param("password", userName)
//      .param("client_id", KEYCLOAK_CLIENT_ID)
//      .param("client_secret", KEYCLOAK_CLIENT_SECRET)
//      .when()
//      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
//      .as(AccessTokenResponse.class).getToken();
//  }
}
