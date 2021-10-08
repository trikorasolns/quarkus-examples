package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloak.clientresource.KeycloakAuthorizationResource;
import com.trikorasolutions.example.keycloak.dto.UserRepresentation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;

@ApplicationScoped
public class UserLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserLogic.class);

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

  public static UserRepresentation toUserRepresentation(UserDto from) {
    return new UserRepresentation(from.givenName, from.familyName, from.email,
      from.enabled, from.userName);
  }

  public static UserDto toUserDto(UserRepresentation from) {
    return new UserDto(from.firstName, from.lastName, from.email,
      from.enabled, from.username);
  }

  public static UserDto from(JsonArray from) {
    // We only parse one user, so it must be stored in position with index 0
    JsonObject toParse;
    try {
       toParse = from.getJsonObject(0);
    }catch (IndexOutOfBoundsException e){
      return null;
    }

    return new UserDto(toParse.getString("firstName"),toParse.getString("lastName"),
      toParse.getString("email"),toParse.getBoolean("enabled"), toParse.getString("username"));
  }

  public static UserDto from(JsonObject from) {
    // Cannot reuse code since keycloak response fields have different keys between
    // admin and user endpoints
    return new UserDto(from.getString("given_name"),from.getString("family_name"),
      from.getString("email"),true, from.getString("preferred_username"));
  }

  /**
   * This function return the information of the current user, a user only can request its own information
   * @param realm
   * @param keycloakSecurityContext
   * @return
   */
  public Uni<UserDto> keycloakUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext) {
    return keycloakClient.userInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID).onItem().transform(jsonObject -> this.from(jsonObject));
  }

  public String getTenant(final JsonWebToken jwt, final String tenantName) {
    return jwt.getClaim(tenantName).toString();
  }

}
