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


curl --location --request  \
POST -k 'https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/auth' \
  --data-urlencode 'scope=openid' \
  --data-urlencode 'response_type=code' \
  --data-urlencode 'client_id=backend-service' \
  --data-urlencode 'redirect_uri=https://localhost:8543/auth/realms/trikorasolutions/account/'


curl --location --request  \
POST -k 'https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/userinfo' \
  --data-urlencode 'scope=openid' \
  --data-urlencode 'response_type=code' \
  --data-urlencode 'client_id=backend-service' \
  --data-urlencode 'redirect_uri=https://localhost:8543/auth/realms/trikorasolutions/account/'


*** MAL?
curl -i  \
POST -k 'https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/userinfo' \
  -H 'Authorization=Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNHVGMHcxcTkxSHVtM3B1cXFsYUVFYlR3dFpwLVRnZ3JNLWE0OTVZX3pzIn0.eyJleHAiOjE2MzMzMzk4MjcsImlhdCI6MTYzMzMzOTUyNywianRpIjoiNzNiZjBkNTUtNDU0My00NWMxLTlmYTMtODcwYzdlZWU1YmM5IiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6ODU0My9hdXRoL3JlYWxtcy90cmlrb3Jhc29sdXRpb25zIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImE4ZmY3ZTZmLTQ5NzQtNGYzYS1hMTRhLWIwZDk2ZTM2YmExMyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImJhY2tlbmQtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiJjOGY3ZDQ0NC1jNmRjLTQ3Y2ItYTRlMS0wOTI5MDMwZDU4OTAiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJhZG1pbiIsImRlZmF1bHQtcm9sZXMtdHJpa29yYXNvbHV0aW9ucyIsInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImM4ZjdkNDQ0LWM2ZGMtNDdjYi1hNGUxLTA5MjkwMzBkNTg5MCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6ImFkbWluQHRyaWtvcmFzb2x1dGlvbnMuY29tIn0.aOcDiqa47i7ObZxA7ClQE5CsTe6sXlFa6BClaMfA891LtCMAK6lxinVd82-ZJR3sMVqMFYqtHQvIPA8xKBAEATqkCzXmWO3d1U-0rw_eymkx6g3OdGdYh4Rw30CEofFqPI02R6m7xXqtvZsBUU8dY9wPbxS-xBeDt8iwk70ROgY6MhhXEok8KUqdqOGXkBepREJVVUmvm9W0vG8V2G7kHkweNBIqjBoftiNS2aFmgHkdu9GWRdBwXnm-S7UffFTu8qpKzCSCXLpNNIZ-6O40eS2n5TTeEU4EP7Yi7790lIv7miy4TdEkhCHKx7-p-MYLvVSLBBQiyioDAE-biCncJw' | jq. \
  --data-urlencode 'realm=trikorasolutions' \
  --data-urlencode 'grant_type=implicit' \
  --data-urlencode 'client_id=backend-service'



  curl -i -k\
  -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNHVGMHcxcTkxSHVtM3B1cXFsYUVFYlR3dFpwLVRnZ3JNLWE0OTVZX3pzIn0.eyJleHAiOjE2MzMzNDIwOTIsImlhdCI6MTYzMzM0MTc5MiwianRpIjoiMzExMWM4YmQtODQzYS00MzQzLWI4NjYtYTg3ZjMxYWRiYjExIiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6ODU0My9hdXRoL3JlYWxtcy90cmlrb3Jhc29sdXRpb25zIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImE3ODczY2UzLTA5MTEtNDgxZi1iZjQxLWUyYWUxNDNhY2M1OCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImJhY2tlbmQtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiJiYzY3MTBlYS1iZmNjLTQxZjAtOWM2NS1kYTRlZDE3MjQ2NWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXRyaWtvcmFzb2x1dGlvbnMiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiLCJjb25maWRlbnRpYWwiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJiYzY3MTBlYS1iZmNjLTQxZjAtOWM2NS1kYTRlZDE3MjQ2NWMiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJKb2huIERvZSIsInByZWZlcnJlZF91c2VybmFtZSI6Impkb2UiLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiRG9lIiwiZW1haWwiOiJqb2huZG9lQHRyaWtvcmFzb2x1dGlvbnMuY29tIn0.FEuGxCrExjibxAPhvfabdFCXVZxYllEMxwsJaadALd03DwP_B-NOMe5GEVSy5e5lM7pzT0GWZdkSStAw97HkmLKszYUCEahjRNYFEqoObSa-uxiKYakp8g4-1pBw7Yu0cQO3KCtfQn993fd4RCrH6BKYtWdj_1BdeHQ25WM3A87iF34LRW3Jksuh1HbKrt-B-d3i805xGdApXhamuQ5TM2SfT9qhdqTuY_cOBpv5GN2yn5aYwowU2PeqInTyIBux0riJu9eyNKYTestpqEiitDa96DC1I-nchfxDIwhYf4hhHS3Qm5lC5-Tbhjs7b4x-kCv8gYaHszi0UmEjAMx3nw" \
    -d 'realm=trikorasolutions' \
  -d 'grant_type=implicit' \
  -d 'client_id=backend-service' \
  "https://localhost:8543/auth/realms/trikorasolutions/protocol/openid-connect/userinfo"
 */