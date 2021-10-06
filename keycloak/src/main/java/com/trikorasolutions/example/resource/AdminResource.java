package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.bl.UserAdminLogic;
import com.trikorasolutions.example.dto.UserDto;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static com.trikorasolutions.example.bl.KeycloakInfo.printKeycloakInfo;
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
  public Uni<Response> checkAccessAdm() {
    // This resource just check the access, so it can  return anything in the response
    return Uni.createFrom()
      .item(Response.ok(this.keycloakSecurityContext.getPrincipal().getName() + "is accessing the service").build());
  }


  @GET
  @Path("/listUsers/{realm}")
  @NoCache
  public Uni<Response> listUsers(@PathParam("realm") String realm) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.info("listUsers: {}", realm);
      printKeycloakInfo(keycloakSecurityContext, Optional.of(jwt));
    }

    return userAdminLogic.listAll(realm, keycloakSecurityContext).onItem()
      .transform(userList -> Response.ok(userList).build());
  }

  @POST
  @Path("/{realm}/users")
  @NoCache
  public Uni<Response> createUser(@PathParam("realm") String realm, final UserDto newUser) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.info("createUser: {}", realm);
      printKeycloakInfo(keycloakSecurityContext, Optional.of(jwt));
    }

    return userAdminLogic.createUser(realm, keycloakSecurityContext, newUser).onItem()
      .transform(userList -> Response.ok(userList).build())
      .onFailure().recoverWithItem(throwable -> Response.ok().status(CONFLICT).build());
  }

  @PUT
  @Path("/{realm}/users")
  @NoCache
  public Uni<Response> updateUser(@PathParam("realm") String realm, final UserDto user) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.info("UPDATE ID: {}", userAdminLogic.updateUser(realm, keycloakSecurityContext,
          userAdminLogic.nameToId(realm, keycloakSecurityContext, user.userName).toString(), user).onItem()
        .transform(userList -> Response.ok(userList).build()));
    }
    return userAdminLogic.nameToId(realm, keycloakSecurityContext, user.userName).onItem()
      .call(userid -> userAdminLogic.updateUser(realm, keycloakSecurityContext, userid, user)).onItem()
      .transform(userList -> Response.ok(userList).build());
  }

  @GET
  @Path("/{realm}/users/{user}")
  @NoCache
  public Uni<Response> getUserInfo(@PathParam("realm") String realm, @PathParam("user") String user){
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("#getUserInfo: {} - {}", user);
    }
    return userAdminLogic.getUserInfo(realm, keycloakSecurityContext, user)
      .onItem().transform(userList -> Response.ok(userList).build());
  }

  @DELETE
  @Path("/{realm}/users/{user}")
  @NoCache
  public Uni<Response> deleteUser(@PathParam("realm") String realm, @PathParam("user") String user){
    if (LOGGER.isDebugEnabled()) {
      LOGGER.info("#deleteUser: {} with ID: {}", user, userAdminLogic.nameToId(realm, keycloakSecurityContext, user));
    }
    return userAdminLogic.nameToId(realm, keycloakSecurityContext, user).onItem()
      .call(userid -> userAdminLogic.deleteUser(realm, keycloakSecurityContext, userid))
      .onItem().transform(userList -> Response.ok(userList).build());
  }

}