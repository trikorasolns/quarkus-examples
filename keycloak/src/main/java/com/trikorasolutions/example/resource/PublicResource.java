package com.trikorasolutions.example.resource;

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
@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(HelloResource.class);

  @Inject
  SecurityIdentity keycloakSecurityContext;

  @Inject
  JsonWebToken jwt;

  @GET
  @Path("/")
  @NoCache
  public Uni<Response> checkAccessPub() {
    // This resource just check the access, so it can  return anything in the response (nothing is requested by the test)
    return Uni.createFrom().item(Response.ok().build());
  }

}