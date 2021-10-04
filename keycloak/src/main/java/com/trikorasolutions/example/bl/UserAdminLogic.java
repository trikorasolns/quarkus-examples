package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloakclient.KeycloakAuthorizationResource;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;

@ApplicationScoped
public class UserAdminLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminLogic.class);

  @RestClient
  KeycloakAuthorizationResource keycloakClient;

  //TODO: Implement this method through a functional interface
  public static UserDto load(SecurityIdentity identity, JsonWebToken jwt) {

    UserDto user = new UserDto(identity.getPrincipal().getName());
    return user.setUserRoles(identity.getRoles()).setUserPermissions(identity.getAttribute("permissions"))
      .setUserCredentials(identity.getCredentials()).setUserName(identity.getPrincipal().getName())
      .setEmail(jwt.getClaim("email")).setEmailVerified(jwt.getClaim("email_verified").toString())
      .setGivenName(jwt.getClaim("given_name")).setPreferredUsername(jwt.getClaim("preferred_username"))
      .setFamilyName(jwt.getClaim("family_name")).setGroups(jwt.getClaim("groups"));
  }

  public int listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("listAll: {}, token {}", realm, KeycloakInfo.getAccessToken("admin"));
    }
    try {
      JsonObject x = keycloakClient.listAll(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(),
        realm, "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID).await().indefinitely();
      LOGGER.info("x: {}", x);

    } catch (RuntimeException ex) {
      LOGGER.error("exception: {}", ex);
    }
    return 1;
  }

//
//  public int authorizeConn() {
//    if (LOGGER.isInfoEnabled()) {
//      LOGGER.info("authorizeConn: ");
//    }
//    try {
//      KeycloakInfo x;
//      keycloakClient.authorize( "openid", "code", KeycloakInfo.KEYCLOAK_CLIENT_ID, "https://localhost:8543/auth/realms/trikorasolutions/account/").await().indefinitely();
////   onItem().invoke(k -> LOGGER.info("listAll: {}", k)).onFailure().recoverWithItem(ex -> {
////       LOGGER.error("exception: {}", ex);
////       return JsonObject.EMPTY_JSON_OBJECT;
////     }).onCancellation().invoke(() -> LOGGER.error("listAll: CANCELED"));
//    } catch (RuntimeException ex) {
//      LOGGER.error("exception: {}", ex);
//    }
//    return 1;
//
//  }


}
