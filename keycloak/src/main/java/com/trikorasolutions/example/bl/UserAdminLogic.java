package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.keycloak.client.bl.KeycloakClientLogic;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserAdminLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminLogic.class);

  @Inject
  KeycloakClientLogic keycloakClientBl;

  public Uni<UserDto> createUser(final String realm, final SecurityIdentity keycloakSecurityContext, final UserDto newUser) {
    return keycloakClientBl.createUser(
        realm, UserLogic.getToken(keycloakSecurityContext), KeycloakInfo.KEYCLOAK_CLIENT_ID, UserLogic.toUserRepresentation(newUser))
      .onFailure(RuntimeException.class).transform(x-> {throw (RuntimeException) x;})
      .onItem().transform(UserLogic::toUserDto);
  }

  public Uni<UserDto> updateUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String userName, final UserDto newUser) {
    return keycloakClientBl.updateUser(
      realm, UserLogic.getToken(keycloakSecurityContext), KeycloakInfo.KEYCLOAK_CLIENT_ID, userName, UserLogic.toUserRepresentation(newUser))
      .onItem().transform(UserLogic::toUserDto);
  }

  public Uni<UserDto> getUserInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String userName) {
    return keycloakClientBl.getUserInfo(
      realm, UserLogic.getToken(keycloakSecurityContext), KeycloakInfo.KEYCLOAK_CLIENT_ID, userName)
      .onItem().transform(UserLogic::toUserDto);
  }

  public Uni<Boolean> deleteUser(final String realm, final SecurityIdentity keycloakSecurityContext, final String userName) {
    return keycloakClientBl.deleteUser(realm, UserLogic.getToken(keycloakSecurityContext), KeycloakInfo.KEYCLOAK_CLIENT_ID, userName);
  }

  public Uni<List<UserDto>> listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {
    return keycloakClientBl.listAll(realm, UserLogic.getToken(keycloakSecurityContext), KeycloakInfo.KEYCLOAK_CLIENT_ID)
      .onItem().transform(userList -> userList.stream()
        .map(UserLogic::toUserDto)
        .collect(Collectors.toList()));
  }

  public Uni<JsonObject> getGroupInfo(final String realm, final SecurityIdentity keycloakSecurityContext, final String name) {
    return keycloakClientBl.getGroupInfo(realm, UserLogic.getToken(keycloakSecurityContext), KeycloakInfo.KEYCLOAK_CLIENT_ID, name);
  }

  public Uni<List<UserDto>> getGroupUsers(final String realm, final SecurityIdentity keycloakSecurityContext, final String groupName) {
    return keycloakClientBl.getUsersForGroup(realm, UserLogic.getToken(keycloakSecurityContext),KeycloakInfo.KEYCLOAK_CLIENT_ID, groupName)
      .onItem().transform(userList -> userList.stream()
        .map(UserLogic::toUserDto)
        .collect(Collectors.toList()));
  }

  public Uni<UserDto> putUserInGroup(final String realm, final SecurityIdentity keycloakSecurityContext,
                                       final String userName, final String groupName){
    return keycloakClientBl.putUserInGroup(realm, UserLogic.getToken(keycloakSecurityContext),KeycloakInfo.KEYCLOAK_CLIENT_ID,
        userName, groupName).onItem().transform(UserLogic::toUserDto);
  }

  public Uni<UserDto> deleteUserFromGroup(final String realm, final SecurityIdentity keycloakSecurityContext,
                                     final String userName, final String groupName){
    return keycloakClientBl.deleteUserFromGroup(realm, UserLogic.getToken(keycloakSecurityContext),KeycloakInfo.KEYCLOAK_CLIENT_ID,
      userName, groupName).onItem().transform(UserLogic::toUserDto);
  }
}
