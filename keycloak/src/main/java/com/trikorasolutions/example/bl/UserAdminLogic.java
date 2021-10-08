package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloak.client.bl.KeycloakClientLogic;
import com.trikorasolutions.example.keycloak.client.clientresource.KeycloakAuthAdminResource;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserAdminLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminLogic.class);

  @RestClient
  KeycloakAuthAdminResource keycloakClient;

  @Inject
  KeycloakClientLogic keycloakClientBl;

  public Uni<JsonArray> listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {
    return keycloakClient.listAll(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID);
  }

  public Uni<UserDto> createUser(final String realm, final SecurityIdentity keycloakSecurityContext, final UserDto newUser) {
    return keycloakClient.createUser(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
        "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, UserLogic.toUserRepresentation(newUser))
      .replaceWith(this.getUserInfo(realm, keycloakSecurityContext, newUser.userName));
  }

  public Uni<JsonArray> updateUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String userId, final UserDto newUser) {
    return keycloakClient.updateUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, userId, UserLogic.toUserRepresentation(newUser));
  }

  public Uni<UserDto> getUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClient.getUserInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, name).onItem().transform(UserLogic::from);
  }

  public Uni<String> nameToId(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClient.getUserInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, name).onItem().transform(userInfo -> {
      if (userInfo != null) return userInfo.get(0).asJsonObject().getString("id");
      else return null;
    });
  }

  public Uni<JsonArray> deleteUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String id) {
    return keycloakClient.deleteUser(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, id);
  }

  public Uni<JsonArray> getGroupInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClient.getGroupInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, name);
  }

  public Uni<String> groupNameToId(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClient.getGroupInfo(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID, name).onItem().transform(userInfo -> {
      if (userInfo != null) return userInfo.get(0).asJsonObject().getString("id");
      else return null;
    });
  }

  /**
   * @param realm
   * @param keycloakSecurityContext
   * @param group
   * @return List of users.
   */
  public Uni<List<UserDto>> getGroupUsers(final String realm, final SecurityIdentity keycloakSecurityContext, final String group) {
    LOGGER.info("ENTRO EN LOGIC");
    return this.groupNameToId(realm, keycloakSecurityContext, group)
      .onItem().invoke(x->LOGGER.info("pinto id :{}",x))
      .onItem()
      .transformToUni(groupId -> keycloakClientBl.getUsersForGroup(realm, keycloakSecurityContext,KeycloakInfo.KEYCLOAK_CLIENT_ID, groupId))
      .onItem()
      .transform(userList -> userList.stream()
        .map(JsonValue::asJsonObject)
        .map(UserLogic::from)
        .collect(Collectors.toList()));
  }



}
