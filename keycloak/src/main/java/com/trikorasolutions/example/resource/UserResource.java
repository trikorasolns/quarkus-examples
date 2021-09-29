package com.trikorasolutions.example.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trikorasolutions.example.bl.UserLogic;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

  @GET
  @Path("/userinfo")
  @NoCache
  public Uni<Response> getUserInfo() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("##################################################");
      LOGGER.debug("########### PRINTING CURRENT USER INFO ###########");
      ObjectMapper mapper = new ObjectMapper();
      LOGGER.info("credentials{}", this.keycloakSecurityContext.getCredentials());
      LOGGER.info("Attributes{}", this.keycloakSecurityContext.getAttributes());
      LOGGER.info("permissions{}", this.keycloakSecurityContext.getAttribute("permissions").toString());
      LOGGER.info("roles{}", this.keycloakSecurityContext.getRoles());
      jwt.getClaimNames().forEach(x -> LOGGER.info("CLAIM {}: {}", x, jwt.getClaim(x).toString()));
      LOGGER.debug("##################################################");
    }

    return Uni.createFrom().item(Response.ok(UserLogic.load(this.keycloakSecurityContext, this.jwt)).build());
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