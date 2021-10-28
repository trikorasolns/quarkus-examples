package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.keycloak.client.clientresource.KeycloakAuthorizationResource;
import com.trikorasolutions.keycloak.client.dto.UserRepresentation;
import com.trikorasolutions.keycloak.client.dto.KeycloakUserRepresentation;
import io.quarkus.security.credential.Credential;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Set;

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
    LOGGER.debug("#toUserRepresentation(UserDto)... {}", from);
    return new UserRepresentation(from.givenName, from.familyName, from.email,
      from.enabled, from.userName);
  }

  public static UserDto toUserDto(KeycloakUserRepresentation from) {
    LOGGER.debug("#toUserDto(UserDto)... {}", from);

    if (from == null) return new UserDto();

    return new UserDto(from.id, from.firstName, from.lastName, from.email,
      from.enabled, from.username);
  }

  public static UserDto from(JsonArray from) {
    LOGGER.debug("#from(JsonArray)... {}", from);
    // We only parse one user, so it must be stored in position with index 0
    JsonObject toParse;

    try {
       toParse = from.getJsonObject(0);
    }catch (IndexOutOfBoundsException e){
      return new UserDto("Unknown User");
    }

    return new UserDto(toParse.getString("firstName"),toParse.getString("lastName"),
      toParse.getString("email"),toParse.getBoolean("enabled"), toParse.getString("username"));
  }

  public static UserDto from(JsonObject from) {
    LOGGER.debug("#from(JsonObject)... {}", from);
    // Cannot reuse code since keycloak response fields have different keys between
    // admin and user endpoints
    if (from.containsKey("given_name")){
      return new UserDto(from.getString("given_name"),from.getString("family_name"),
        from.getString("email"),true, from.getString("preferred_username"));
    }else if (!from.containsKey("lastName")){ // Admin user do not have family name
      return new UserDto(from.getString("firstName"), "IS_CONFIDENTIAL",
        from.getString("email"), false, from.getString("username"));
    }else{
      return new UserDto(from.getString("firstName"),from.getString("lastName"),
        from.getString("email"),from.getBoolean("enabled"), from.getString("username"));
    }
  }

  public static String getToken(SecurityIdentity keycloakSecurityContext){
    LOGGER.debug("#getToken(SecurityIdentity)... {}", keycloakSecurityContext);
    return keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken();
  }

  public Uni<UserDto> keycloakUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext) {
    LOGGER.debug("#keycloakUserInfo(String)... {}", realm);
    return keycloakClient.userInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID).onItem().transform(jsonObject -> this.from(jsonObject));
  }

  public String getTenant(final JsonWebToken jwt, final String tenantName) {
    LOGGER.debug("#getTenant(String)... {}", tenantName);
    return jwt.getClaim(tenantName).toString();
  }
}
