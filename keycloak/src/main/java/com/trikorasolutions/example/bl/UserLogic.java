package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloakclient.KeycloakAuthorizationResource;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
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


  /**
   *
   * @param realm
   * @param keycloakSecurityContext
   * @return
   */
  public Uni<JsonObject> keycloakUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext) {
    return keycloakClient.userInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID);
  }


}
