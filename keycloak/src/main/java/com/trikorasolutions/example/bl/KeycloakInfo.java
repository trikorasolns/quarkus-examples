package com.trikorasolutions.example.bl;

import io.quarkus.oidc.OidcConfigurationMetadata;
import io.quarkus.security.identity.SecurityIdentity;
import io.restassured.RestAssured;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class KeycloakInfo {
  private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakInfo.class);
  //  protected static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "https://localhost:8543/auth");
  protected static final String KEYCLOAK_SERVER_URL = System.getProperty("keycloak.url", "http://localhost:8090/auth");
  protected static final String KEYCLOAK_REALM = "trikorasolutions";
  protected static final String KEYCLOAK_CLIENT_SECRET = "6e521ebe-e300-450f-811a-a08adc42ec4a";
  protected static final String KEYCLOAK_CLIENT_ID = "backend-service";

  public static void printKeycloakInfo(final SecurityIdentity keycloakSecurityContext, final Optional<JsonWebToken> jwt) {
    LOGGER.warn("##################################################");
    LOGGER.warn("########### PRINTING CURRENT USER INFO ###########");
    LOGGER.warn("credentials: {}", keycloakSecurityContext.getCredentials());
    keycloakSecurityContext.getCredentials().forEach(c -> LOGGER.warn("credential: {}", c));
    LOGGER.warn("credential[AccessTokenCredential]: {}", keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken());    LOGGER.warn("Attributes: {}", keycloakSecurityContext.getAttributes());
    keycloakSecurityContext.getAttributes().forEach((k, v) -> LOGGER.warn("attribute {}: {}", k, v));
    ((OidcConfigurationMetadata)keycloakSecurityContext.getAttribute("configuration-metadata")).getPropertyNames().forEach(property -> LOGGER.warn("property: {}={}", property,((OidcConfigurationMetadata)keycloakSecurityContext.getAttribute("configuration-metadata")).get(property)));
    LOGGER.warn("permissions: {}", keycloakSecurityContext.getAttribute("permissions").toString());
    LOGGER.warn("roles: {}", keycloakSecurityContext.getRoles());
    LOGGER.warn("principal: {}", keycloakSecurityContext.getPrincipal().getName());
    LOGGER.warn("########### JWT ###########");
    jwt.get().getClaimNames().forEach(x -> LOGGER.info("CLAIM {}: {}", x, jwt.get().getClaim(x).toString()));
    LOGGER.warn("##################################################");
    LOGGER.warn("r: {}", keycloakSecurityContext.getRoles());
  }

  public static String getAccessToken(String userName) {
    return RestAssured.given().param("grant_type", "password").param("username", userName).param("password", userName)
      .param("client_id", KEYCLOAK_CLIENT_ID).param("client_secret", KEYCLOAK_CLIENT_SECRET).when()
      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
      .as(AccessTokenResponse.class).getToken();
  }
}
