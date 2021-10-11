package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.bl.KeycloakInfo;
import com.trikorasolutions.example.bl.UserAdminLogic;
import com.trikorasolutions.example.bl.UserLogic;
import com.trikorasolutions.example.dto.UserDto;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  JsonWebToken jwt;

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
  public Uni<RestResponse<JsonArray>> listUsers(@PathParam("realm") String realm) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#listUsers: realm name{}", realm);
    }
    return userAdminLogic.listAll(realm, keycloakSecurityContext).onItem()
      .transform(userList -> RestResponse.ResponseBuilder.ok(userList).build());
  }

  @POST
  @Path("/{realm}/users")
  @NoCache
  public Uni<RestResponse<UserDto>> createUser(@PathParam("realm") String realm, final UserDto newUser) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#createUser: username{}", newUser.userName);
    }
    return userAdminLogic.createUser(realm, keycloakSecurityContext, newUser).onItem().transform(userDto -> {
      LOGGER.debug("User created correctly: {}", userDto);
      return RestResponse.ResponseBuilder.ok(userDto).build();
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.debug("Error when creating user with exception:{}", throwable);
      return RestResponse.ResponseBuilder.ok(new UserDto()).status(CONFLICT).build();
    });
  }

  @PUT
  @Path("/{realm}/users")
  @NoCache
  public Uni<RestResponse<UserDto>> updateUser(@PathParam("realm") String realm, final UserDto user) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#updateUser username: {}", user.userName);
    }

    return userAdminLogic.nameToId(realm, keycloakSecurityContext, user.userName).onItem()
      .call(userid -> userAdminLogic.updateUser(realm, keycloakSecurityContext, userid, user))
      .replaceWith(userAdminLogic.getUserInfo(realm, keycloakSecurityContext, user.userName)).onItem()
      .transform(userDto -> RestResponse.ResponseBuilder.ok(userDto).build()).onFailure().recoverWithItem(
        throwable -> RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build());
  }

  @GET
  @Path("/{realm}/users/{user}")
  @NoCache
  public Uni<RestResponse<UserDto>> getUserInfo(@PathParam("realm") String realm, @PathParam("user") String user) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("#getUserInfo: {}", user);
    }
    return userAdminLogic.getUserInfo(realm, keycloakSecurityContext, user).onItem().transform(userDto -> {
      if (userDto == null) {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build();
      } else {
        return RestResponse.ResponseBuilder.ok(userDto).build();
      }
    }).onFailure().recoverWithItem(throwable -> RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build());
  }

  @DELETE
  @Path("/{realm}/users/{user}")
  @NoCache
  public Uni<RestResponse<Void>> deleteUser(@PathParam("realm") String realm, @PathParam("user") String user) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.info("#deleteUser: {} with ID: {}", user, userAdminLogic.nameToId(realm, keycloakSecurityContext, user));
    }
    return userAdminLogic.nameToId(realm, keycloakSecurityContext, user).onItem()
      .call(userid -> userAdminLogic.deleteUser(realm, keycloakSecurityContext, userid)).onItem()
      .transform(userDto -> RestResponse.ResponseBuilder.ok().build()).onFailure()
      .recoverWithItem(throwable -> RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND).build());

  }

  @GET
  @Path("/{realm}/groups/{group}")
  @NoCache
  public Uni<RestResponse<JsonArray>> getGroupInfo(@PathParam("realm") String realm, @PathParam("group") String group) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#getGroupInfo: {}", group);
    }
    return userAdminLogic.getGroupInfo(realm, keycloakSecurityContext, group).onItem().transform(groupArray -> {
      if (groupArray == null) {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, JsonArray.EMPTY_JSON_ARRAY).build();
      } else {
        return RestResponse.ResponseBuilder.ok(groupArray).build();
      }
    }).onFailure().recoverWithItem(
      throwable -> RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, JsonArray.EMPTY_JSON_ARRAY).build());
  }

  @GET
  @Path("/{realm}/groups/{group}/listUsers")
  @NoCache
  public Uni<RestResponse<List<UserDto>>> listGroupUsers(@PathParam("realm") String realm, @PathParam("group") String group) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#listGroupUsers: realm name{}-{}", realm, group);
    }
    List<UserDto> emptyUserDtoList = new ArrayList<>();

    return userAdminLogic.getGroupUsers(realm, keycloakSecurityContext, group).onItem().transform(userDtoList -> {
      if (userDtoList == null) {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, emptyUserDtoList).build();
      } else {
        return RestResponse.ResponseBuilder.ok(userDtoList).build();
      }
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.debug("ES FAIL{}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, emptyUserDtoList).build();
    });
  }

  @PUT
  @Path("/{realm}/users/{user}/groups/{group}")
  @NoCache
  public Uni<RestResponse<UserDto>> putUserInGroup(@PathParam("realm") String realm, @PathParam("user") String user,
                                                   @PathParam("group") String group) {
    return userAdminLogic.putUserInGroup(realm, keycloakSecurityContext, user, group).onItem().transform(userDto -> {
      if (userDto == null) {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build();
      } else {
        return RestResponse.ResponseBuilder.ok(userDto).build();
      }
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.debug("ES FAIL{}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build();
    });
  }

  @DELETE
  @Path("/{realm}/users/{user}/groups/{group}")
  @NoCache
  public Uni<RestResponse<UserDto>> deleteUserFromGroup(@PathParam("realm") String realm, @PathParam("user") String user,
                                                   @PathParam("group") String group) {
    return userAdminLogic.deleteUserFromGroup(realm, keycloakSecurityContext, user, group).onItem().transform(userDto -> {
      if (userDto == null) {
        return RestResponse.ResponseBuilder.create(RestResponse.Status.NOT_FOUND, new UserDto()).build();
      } else {
        return RestResponse.ResponseBuilder.ok(userDto).build();
      }
    }).onFailure().recoverWithItem(throwable -> {
      LOGGER.debug("ES FAIL{}", throwable.getMessage());
      return RestResponse.ResponseBuilder.create(RestResponse.Status.BAD_REQUEST, new UserDto()).build();
    });
  }

}