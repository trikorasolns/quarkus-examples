package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloak.client.bl.KeycloakClientLogic;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
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

  @Inject
  KeycloakClientLogic keycloakClientBl;

  public Uni<UserDto> createUser(final String realm, final SecurityIdentity keycloakSecurityContext, final UserDto newUser) {
    return keycloakClientBl.createUser(
        realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, UserLogic.toUserRepresentation(newUser))
      .replaceWith(this.getUserInfo(realm, keycloakSecurityContext, newUser.userName));
  }

  public Uni<JsonArray> updateUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String userId, final UserDto newUser) {
    return keycloakClientBl.updateUser(
      realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, userId, UserLogic.toUserRepresentation(newUser));
  }

  public Uni<UserDto> getUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClientBl.getUserInfo(
      realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, name)
      .onItem().transform(UserLogic::from);
  }

  public Uni<String> nameToId(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClientBl.getUserInfo(
      realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, name)
      .onItem().transform(userInfo -> {
      if (userInfo != null) return userInfo.get(0).asJsonObject().getString("id");
      else return null;
    });
  }

  public Uni<JsonArray> deleteUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String id) {
    return keycloakClientBl.deleteUser(realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, id);
  }

  public Uni<JsonArray> listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {
    return keycloakClientBl.listAll(realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID);
  }

  public Uni<JsonArray> getGroupInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClientBl.getGroupInfo(realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, name);
  }

  public Uni<String> groupNameToId(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClientBl.getGroupInfo(realm, keycloakSecurityContext, KeycloakInfo.KEYCLOAK_CLIENT_ID, name).onItem().transform(userInfo -> {
      if (userInfo != null) return userInfo.get(0).asJsonObject().getString("id");
      else return null;
    });
  }

  /**
   * Return a list of users in our DTO format, containing the user of a certain group
   *
   * @param realm
   * @param keycloakSecurityContext
   * @param group
   * @return List of users.
   */
  public Uni<List<UserDto>> getGroupUsers(final String realm, final SecurityIdentity keycloakSecurityContext, final String group) {
    return this.groupNameToId(realm, keycloakSecurityContext, group)
      .onItem()
      .transformToUni(groupId -> keycloakClientBl.getUsersForGroup(realm, keycloakSecurityContext,KeycloakInfo.KEYCLOAK_CLIENT_ID, groupId))
      .onItem()
      .transform(userList -> userList.stream()
        .map(JsonValue::asJsonObject)
        .map(UserLogic::from)
        .collect(Collectors.toList()));
  }

  public Uni<UserDto> putUserInGroup(final String realm, final SecurityIdentity keycloakSecurityContext,
                                       final String userName, final String groupName){

    Uni<String> userId = this.nameToId(realm, keycloakSecurityContext,userName);
    Uni<String> groupId = this.groupNameToId(realm, keycloakSecurityContext, groupName);
    Uni<Tuple2<String, String>> combinedUniTuple = Uni.combine().all().unis(userId, groupId).asTuple();

    return combinedUniTuple.onItem()
      .transformToUni(tuple2 -> keycloakClientBl.putUserInGroup(realm, keycloakSecurityContext,KeycloakInfo.KEYCLOAK_CLIENT_ID,
        tuple2.getItem1(),tuple2.getItem2()))
      .replaceWith( this.getUserInfo(realm,keycloakSecurityContext, userName));
  }

  public Uni<UserDto> deleteUserFromGroup(final String realm, final SecurityIdentity keycloakSecurityContext,
                                     final String userName, final String groupName){

    Uni<String> userId = this.nameToId(realm, keycloakSecurityContext,userName);
    Uni<String> groupId = this.groupNameToId(realm, keycloakSecurityContext, groupName);
    Uni<Tuple2<String, String>> combinedUniTuple = Uni.combine().all().unis(userId, groupId).asTuple();

    return combinedUniTuple.onItem()
      .transformToUni(tuple2 -> keycloakClientBl.deleteUserFromGroup(realm, keycloakSecurityContext,KeycloakInfo.KEYCLOAK_CLIENT_ID,
        tuple2.getItem1(),tuple2.getItem2()))
      .replaceWith( this.getUserInfo(realm,keycloakSecurityContext, userName));
  }

}
