package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.bl.UserLogic;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
  public Uni<Response> getUserInfo() {
    return Uni.createFrom().item(Response.ok(UserLogic.load(this.keycloakSecurityContext, this.jwt)).build());
  }

  @GET
  @Path("/kcuserinfo/{realm}")
  @NoCache
  public Uni<Response> getUserInfoKC(@PathParam("realm") String realm) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("listUsers: {}", realm);
    }

    return user.keycloakUserInfo(realm,keycloakSecurityContext).onItem().transform(userInfo->Response.ok(userInfo).build());
  }

  @GET
  @Path("/roles")
  @NoCache
  public Uni<Response> getRoles() {
    return Uni.createFrom().item(Response.ok(UserLogic.load(this.keycloakSecurityContext, this.jwt)).build());
  }

  @GET
  @Path("/me")
  @NoCache
  public Uni<Response> checkAccessUsr(){
    // This resource just check the access, so it can  return anything in the response
    return Uni.createFrom().item(Response.ok(this.keycloakSecurityContext.getPrincipal().getName()
      + "is accessing the service").build());
  }
}