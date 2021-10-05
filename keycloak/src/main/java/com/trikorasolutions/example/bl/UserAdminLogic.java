package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloak.clientresource.KeycloakAuthAdminResource;
import com.trikorasolutions.example.keycloak.dto.UserRepresentation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;

@ApplicationScoped
public class UserAdminLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminLogic.class);

  @RestClient
  KeycloakAuthAdminResource keycloakClient;


  public Uni<JsonArray>  listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {

    return keycloakClient.listAll(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(),
        realm, "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID);
  }

  public Uni<JsonArray> createUser(final String realm, final SecurityIdentity keycloakSecurityContext,
                                   final UserDto newUser) {
//    LOGGER.info("New user (in logic) : {}", newUser.toString());

    return keycloakClient.createUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(),
      realm, "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, UserLogic.toUserRepresentation(newUser));
  }

  public Uni<JsonArray> updateUser(final String realm, final SecurityIdentity keycloakSecurityContext,
                                   final String userId, final UserDto newUser) {
//    LOGGER.info("Updated user (in logic) : {}", newUser.toString());

    return keycloakClient.updateUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(),
      realm, "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, userId, UserLogic.toUserRepresentation(newUser));
  }

}
