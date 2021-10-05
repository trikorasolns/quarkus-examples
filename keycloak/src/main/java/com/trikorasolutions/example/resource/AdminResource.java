package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.bl.UserAdminLogic;
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

import javax.ws.rs.PathParam;

import java.util.Optional;

import static com.trikorasolutions.example.bl.KeycloakInfo.printKeycloakInfo;

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
  UserAdminLogic user;

  @GET
  @Path("/")
  @NoCache
  public Uni<Response> checkAccessAdm() {
    // This resource just check the access, so it can  return anything in the response
    return Uni.createFrom().item(Response.ok(this.keycloakSecurityContext.getPrincipal().getName()
      + "is accessing the service").build());
  }


  @GET
  @Path("/listUsers/{realm}")
  @NoCache
  public Uni<Response> listUsers(@PathParam("realm") String realm) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("listUsers: {}", realm);
      printKeycloakInfo(keycloakSecurityContext, Optional.of(jwt));
    }

    return user.listAll(realm, keycloakSecurityContext).onItem().transform(userList->Response.ok(userList).build());
  }

}