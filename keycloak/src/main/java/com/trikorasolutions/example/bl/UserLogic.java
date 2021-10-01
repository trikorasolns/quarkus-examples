package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloakclient.KeycloakUsersResource;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

@ApplicationScoped
public class UserLogic  {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserLogic.class);

  @RestClient
  KeycloakUsersResource keycloakClient;

  //TODO: Implement this method through a functional interface
  public static UserDto load(SecurityIdentity identity, JsonWebToken jwt) {

    UserDto user = new UserDto(identity.getPrincipal().getName());
    return user.setUserRoles(identity.getRoles())
      .setUserPermissions(identity.getAttribute("permissions"))
      .setUserCredentials(identity.getCredentials())
      .setUserName(identity.getPrincipal().getName())
      .setEmail(jwt.getClaim("email"))
      .setEmailVerified(jwt.getClaim("email_verified").toString())
      .setGivenName(jwt.getClaim("given_name"))
      .setPreferredUsername(jwt.getClaim("preferred_username"))
      .setFamilyName(jwt.getClaim("family_name"))
      .setGroups(jwt.getClaim("groups"))
    ;
  }

 // public Uni<List<UserDto>> listAll(String realm) {
 public int listAll(String realm) {
   if (LOGGER.isInfoEnabled()) {
     LOGGER.info("listAll: {}", realm);
   }
   try {KeycloakInfo x;
     keycloakClient.listAll(realm,"implicit",KeycloakInfo.KEYCLOAK_CLIENT_ID,KeycloakInfo.KEYCLOAK_CLIENT_SECRET).await().indefinitely();
//   onItem().invoke(k -> LOGGER.info("listAll: {}", k)).onFailure().recoverWithItem(ex -> {
//       LOGGER.error("exception: {}", ex);
//       return JsonObject.EMPTY_JSON_OBJECT;
//     }).onCancellation().invoke(() -> LOGGER.error("listAll: CANCELED"));
   } catch(RuntimeException ex) {
     LOGGER.error("exception: {}", ex);
   }
   return 1;

  }
}