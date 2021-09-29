package com.trikorasolutions.example;

import io.restassured.RestAssured;
import org.keycloak.representations.AccessTokenResponse;

public class KeycloakInfo {
  private static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
  private static final String KEYCLOAK_REALM = "quarkus";

  public static String getAccessToken(String userName) {
    return RestAssured.given().param("grant_type", "password").param("username", userName).param("password", userName)
      .param("client_id", "backend-service").param("client_secret", "secret").when()
      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
      .as(AccessTokenResponse.class).getToken();
  }
}
