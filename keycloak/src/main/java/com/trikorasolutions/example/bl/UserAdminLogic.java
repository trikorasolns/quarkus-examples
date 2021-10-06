package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloak.clientresource.KeycloakAuthAdminResource;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;

@ApplicationScoped
public class UserAdminLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminLogic.class);

  @RestClient
  KeycloakAuthAdminResource keycloakClient;

  public Uni<JsonArray> listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {

    return keycloakClient.listAll(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID);
  }

  public Uni<UserDto> createUser(final String realm, final SecurityIdentity keycloakSecurityContext, final UserDto newUser) {
    return keycloakClient.createUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, UserLogic.toUserRepresentation(newUser)).onItem().transform(jsonValues -> UserDto::fromKeycloakJso);
  }

  public Uni<JsonArray> updateUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String userId, final UserDto newUser) {
    return keycloakClient.updateUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, userId, UserLogic.toUserRepresentation(newUser));
  }

  public Uni<JsonArray> getUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {

    return keycloakClient.getUserInfo(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
        "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, name)
      ;
  }
  public Uni<String> nameToId(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {

    return keycloakClient.getUserInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, name)
    .onItem().transform(userInfo -> userInfo.get(0).asJsonObject().getString("id"));
  }

  public Uni<JsonArray> deleteUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String id) {

    return keycloakClient.deleteUser(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
        "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, id);
  }

}
