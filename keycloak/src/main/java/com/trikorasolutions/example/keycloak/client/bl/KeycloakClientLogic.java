package com.trikorasolutions.example.keycloak.client.bl;


import com.trikorasolutions.example.keycloak.client.clientresource.KeycloakAuthAdminResource;
import com.trikorasolutions.example.keycloak.client.dto.UserRepresentation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;


@ApplicationScoped
public class KeycloakClientLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakClientLogic.class);

  @RestClient
  KeycloakAuthAdminResource keycloakClient;

  public Uni<JsonArray> createUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId, final UserRepresentation newUser) {
    return keycloakClient.createUser(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
        "implicit", keycloakClientId , newUser);
  }

  public Uni<JsonArray> updateUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId,  final String userId, final UserRepresentation newUser) {
    return keycloakClient.updateUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, userId, newUser);
  }

  public Uni<JsonArray> getUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId, final String name) {
    return keycloakClient.getUserInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, name);
  }

  public Uni<JsonArray> deleteUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId, final String id) {
    return keycloakClient.deleteUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, id);
  }

  public Uni<JsonArray> listAll(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId) {
    return keycloakClient.listAll(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId);
  }

  public Uni<JsonArray> getGroupInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId, final String name) {
    return keycloakClient.getGroupInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, name);
  }

  public Uni<JsonArray> getUsersForGroup(final String realm, final SecurityIdentity keycloakSecurityContext, final String keycloakClientId, final String groupId) {
    return keycloakClient.getGroupUsers(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, groupId);
  }

  public Uni<JsonArray> putUserInGroup(final String realm, final SecurityIdentity keycloakSecurityContext,
                                       final String keycloakClientId, final String userId, final String groupId) {
    return keycloakClient.putUserInGroup(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, userId, groupId);
  }

  public Uni<JsonArray> deleteUserFromGroup(final String realm, final SecurityIdentity keycloakSecurityContext,
                                       final String keycloakClientId, final String userId, final String groupId) {
    return keycloakClient.deleteUserFromGroup(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, userId, groupId);
  }
}
