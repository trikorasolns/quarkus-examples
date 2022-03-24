package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.bl.UserAdminLogic;
import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.keycloak.client.exception.*;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;

@ApplicationScoped
@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);

  @Inject
  SecurityIdentity keycloakSecurityContext;

  @Inject
  UserAdminLogic userAdminLogic;

  @GET
  @Path("/")
  @NoCache
  public Uni<RestResponse<String>> checkAccessAdm() {
    // This resource just check the access, so it can  return anything in the response
    return Uni.createFrom().item(RestResponse.ResponseBuilder.ok(this.keycloakSecurityContext.getPrincipal().getName() + "is accessing the service").build());
  }

  @GET
  @Path("/listUsers/{realm}")
  @NoCache
  public Uni<RestResponse<List<UserDto>>> listUsers(@PathParam("realm") String realm) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#listUsers: realm name{}", realm);
    }
    return userAdminLogic.listAll(realm, keycloakSecurityContext).onItem().transform(userList -> {
      LOGGER.debug("User list fetched correctly in realm : {}",realm);
      return RestResponse.ResponseBuilder.ok(userList).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.info("Error when listing the users with exception:{}", throwable);
      return RestResponse.ResponseBuilder.ok((List<UserDto>) new ArrayList<UserDto>()).status(CONFLICT).build();
    });
  }

  @POST
  @Path("/{realm}/users")
  @NoCache
  public Uni<RestResponse<UserDto>> createUser(@PathParam("realm") String realm, final UserDto newUser) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#createUser, username: {}", newUser.userName);
    }
    return userAdminLogic.createUser(realm, keycloakSecurityContext, newUser).onItem().transform(userDto -> {
      LOGGER.debug("User created correctly: {}", userDto);
      return RestResponse.ResponseBuilder.ok(userDto).build();
    }).onFailure(DuplicatedUserException.class).recoverWithItem(throwable -> {
      LOGGER.warn("DuplicatedUserException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.CONFLICT, new UserDto()).build();

    }).onFailure(InvalidTokenException.class).recoverWithItem(throwable -> {
      LOGGER.warn("InvalidTokenException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.UNAUTHORIZED, new UserDto()).build();

    }).onFailure(ClientNotFoundException.class).recoverWithItem(throwable -> {
      LOGGER.warn("ClientNotFoundException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build();

    }).onFailure(ArgumentsFormatException.class).recoverWithItem(throwable -> {
      LOGGER.warn("ArgumentsFormatException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.ok(new UserDto()).status(BAD_REQUEST).build();
    });
  }

  @PUT
  @Path("/{realm}/users")
  @NoCache
  public Uni<RestResponse<UserDto>> updateUser(@PathParam("realm") String realm, final UserDto user) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#updateUser username: {}", user.userName);
    }
    return userAdminLogic.updateUser(realm, keycloakSecurityContext, user.userName, user)
      .onItem().transform(userDto ->{
        LOGGER.debug("#updateUser, username: {}", user.userName);
        return RestResponse.ResponseBuilder.ok(userDto).build();
      }).onFailure(NoSuchUserException.class).recoverWithItem(throwable -> {
        LOGGER.warn("NoSuchUserException: {}", throwable.getMessage());
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build();
      }).onFailure().recoverWithItem(throwable ->{
        LOGGER.warn("Error when updating user with exception:{}", throwable);
        return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build();
      });
  }

  @GET
  @Path("/{realm}/users/{user}")
  @NoCache
  public Uni<RestResponse<UserDto>> getUserInfo(@PathParam("realm") String realm, @PathParam("user") String user) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#getUserInfo: {}", user);
    }
    return userAdminLogic.getUserInfo(realm, keycloakSecurityContext, user).onItem().transform(userDto -> {
      LOGGER.debug("User info fetched correctly: {}", userDto);
      return RestResponse.ResponseBuilder.ok(userDto).build();
    }).onFailure(NoSuchUserException.class).recoverWithItem(throwable -> {
      LOGGER.warn("NoSuchUserException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.warn("Error when fetching user info with exception:{}", throwable);
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build();
    });
  }

  @DELETE
  @Path("/{realm}/users/{user}")
  @NoCache
  public Uni<RestResponse<Boolean>> deleteUser(@PathParam("realm") String realm, @PathParam("user") String user) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.info("#deleteUser: {} with ID: {}", user);
    }
    return userAdminLogic.deleteUser(realm, keycloakSecurityContext, user).onItem().transform(isDeleted ->{
        LOGGER.debug("User deleted correctly: {}", isDeleted);
        return RestResponse.ResponseBuilder.ok(isDeleted).build();
      }).onFailure(NoSuchUserException.class).recoverWithItem(throwable -> {
        LOGGER.debug("NoSuchUserException: {}", throwable.getMessage());
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, Boolean.FALSE).build();
      }).onFailure().recoverWithItem(throwable ->{
        // Do not print anything, deleting an unknown user is not consider an error
        LOGGER.warn("Error when deleting user with exception:{}", throwable);
        return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, Boolean.FALSE).build();
    });
  }

  @GET
  @Path("/{realm}/groups/{group}")
  @NoCache
  public Uni<RestResponse<JsonObject>> getGroupInfo(@PathParam("realm") String realm, @PathParam("group") String group) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#getGroupInfo: {}", group);
    }
    return userAdminLogic.getGroupInfo(realm, keycloakSecurityContext, group).onItem().transform(groupArray -> {
      LOGGER.debug("Group info fetched correctly: {}", group);
      return RestResponse.ResponseBuilder.ok(groupArray).build();
    }).onFailure(NoSuchGroupException.class).recoverWithItem(throwable -> {
      LOGGER.info("NoSuchGroupException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, JsonObject.EMPTY_JSON_OBJECT).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.warn("Error when fetching group info with exception:{}", throwable);
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, JsonObject.EMPTY_JSON_OBJECT).build();
    });
  }

  @GET
  @Path("/{realm}/groups/{group}/listUsers")
  @NoCache
  public Uni<RestResponse<List<UserDto>>> listGroupUsers(@PathParam("realm") String realm, @PathParam("group") String group) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#listGroupUsers: realm name {}-{}", realm, group);
    }
    return userAdminLogic.getGroupUsers(realm, keycloakSecurityContext, group).onItem().transform(userDtoList -> {
      LOGGER.debug("Group users fetched correctly: {}", group);
      return RestResponse.ResponseBuilder.ok(userDtoList).build();
    }).onFailure(NoSuchGroupException.class).recoverWithItem(throwable -> {
      LOGGER.info("NoSuchGroupException: {}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, (List<UserDto>) new ArrayList<UserDto>()).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.info("Error when fetching group users with exception:{}", throwable);
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, (List<UserDto>) new ArrayList<UserDto>()).build();
    });
  }

  @PUT
  @Path("/{realm}/users/{user}/groups/{group}")
  @NoCache
  public Uni<RestResponse<UserDto>> putUserInGroup(@PathParam("realm") String realm, @PathParam("user") String user,
                                                   @PathParam("group") String group) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#putUserInGroup: group {}", group);
    }
    return userAdminLogic.putUserInGroup(realm, keycloakSecurityContext, user, group).onItem().transform(userDto -> {
      LOGGER.debug("User has been added to the group: {}", group);
      return RestResponse.ResponseBuilder.ok(userDto).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.debug("Error when adding user to the group with exception:{}", throwable);
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build();
    });
  }

  @DELETE
  @Path("/{realm}/users/{user}/groups/{group}")
  @NoCache
  public Uni<RestResponse<UserDto>> deleteUserFromGroup(@PathParam("realm") String realm, @PathParam("user") String user,
                                                   @PathParam("group") String group) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#deleteUserFromGroup: group {}", group);
    }
    return userAdminLogic.deleteUserFromGroup(realm, keycloakSecurityContext, user, group).onItem().transform(userDto -> {
      LOGGER.debug("User has been removed from the group: {}", group);
      return RestResponse.ResponseBuilder.ok(userDto).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.debug("Error when removing user from the group with exception:{}", throwable);
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build();
    });
  }
}
