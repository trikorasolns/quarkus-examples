package com.trikorasolutions.example.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    ObjectMapper mapper = new ObjectMapper();
    LOGGER.warn("credentials: {}", keycloakSecurityContext.getCredentials());
    keycloakSecurityContext.getCredentials().forEach(c -> LOGGER.warn("credential: {}", c));
    LOGGER.warn("credential[AccessTokenCredential]: {}", keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken());
    LOGGER.warn("Attributes: {}", keycloakSecurityContext.getAttributes());
    keycloakSecurityContext.getAttributes().forEach((k, v) -> LOGGER.warn("attribute {}: {}", k, v));
    LOGGER.warn("permissions: {}", keycloakSecurityContext.getAttribute("permissions").toString());
    LOGGER.warn("roles: {}", keycloakSecurityContext.getRoles());
    LOGGER.warn("########### JWT ###########");
    jwt.get().getClaimNames().forEach(x -> LOGGER.info("CLAIM {}: {}", x, jwt.get().getClaim(x).toString()));
    LOGGER.warn("##################################################");
  }

  public static String getAuthToken() {
    return RestAssured.given().param("scope", "openid").param("response_type", "code")
      .param("client_id", KEYCLOAK_CLIENT_ID).param("redirect_uri", KEYCLOAK_SERVER_URL).when()
      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/auth")
      .as(AccessTokenResponse.class).getToken();
  }

  public static String getAccessToken(String userName) {
    return RestAssured.given().param("grant_type", "password").param("username", userName).param("password", userName)
      .param("client_id", KEYCLOAK_CLIENT_ID).param("client_secret", KEYCLOAK_CLIENT_SECRET).when()
      .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
      .as(AccessTokenResponse.class).getToken();
  }
}


/* --data-urlencode == Querry param , -k rely in the host

BEARER_TOKEN=$(\
curl --silent --location --request \
POST -k 'https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/token' \
  --data-urlencode 'password=jdoe' \
  --data-urlencode 'username=jdoe' \
  --data-urlencode 'client_id=backend-service' \
  --data-urlencode 'grant_type=password' \
  --data-urlencode 'client_secret=6e521ebe-e300-450f-811a-a08adc42ec4a'\
| jq -r '.access_token')


BEARER_TOKEN=$(\
curl --silent --location --request \
POST -k 'https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/token' \
  --data-urlencode 'client_id=backend-service' \
  --data-urlencode 'grant_type=client_credentials' \
  --data-urlencode 'client_secret=6e521ebe-e300-450f-811a-a08adc42ec4a' \
| jq -r '.access_token')

curl -i -k  -H "Authorization: Bearer ${BEARER_TOKEN}"     -d 'realm=trikorasolutions'   -d 'grant_type=implicit'   -d 'client_id=backend-service'   "https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/userinfo"

curl --location -i --request POST 'http://localhost:8090/auth/admin/realms/trikorasolutions/users' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer ${BEARER_TOKEN}' \
--data-raw '{"firstName":"Sergey","lastName":"Kargopolov", "email":"test@test.com", "enabled":"true", "username":"app-user"}'




 */