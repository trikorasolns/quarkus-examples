package com.trikorasolutions.example.resource;

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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@ApplicationScoped
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

  @Inject
  SecurityIdentity keycloakSecurityContext;

  @Inject
  JsonWebToken jwt;

  @Inject
  UserLogic user;

  @GET
  @Path("/userinfo")
  @NoCache
  public Uni<RestResponse<UserDto>>  getUserInfo() {
    return Uni.createFrom().item(RestResponse.ResponseBuilder.ok(
      UserLogic.load(this.keycloakSecurityContext, this.jwt)).build());
  }

  @GET
  @Path("/tenant")
  @NoCache
  public Uni<RestResponse<String>>  getUserTenant() {
    return Uni.createFrom().item(RestResponse.ResponseBuilder.ok(
      user.getTenant(this.jwt, "tkr_tenant")).build());
  }

  @GET
  @Path("/kcuserinfo/{realm}")
  @NoCache
  public Uni<RestResponse<UserDto>>  getUserInfoKC(@PathParam("realm") String realm) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#listUsers: realm name{}", realm);
    }
    return user.keycloakUserInfo(realm,keycloakSecurityContext)
      .onItem().transform(userInfo->RestResponse.ResponseBuilder.ok(userInfo).build());
  }

  @GET
  @Path("/roles")
  @NoCache
  public Uni<RestResponse<UserDto>>  getRoles() {
    return Uni.createFrom().item(RestResponse.ResponseBuilder.ok(
      UserLogic.load(this.keycloakSecurityContext, this.jwt)).build());
  }

  @GET
  @Path("/me")
  @NoCache
  public Uni<RestResponse<String>>  checkAccessUsr(){
    // This resource just check the access, so it can  return anything in the response
    return Uni.createFrom().item(RestResponse.ResponseBuilder.ok(this.keycloakSecurityContext.getPrincipal().getName()
      + "is accessing the service").build());
  }
}