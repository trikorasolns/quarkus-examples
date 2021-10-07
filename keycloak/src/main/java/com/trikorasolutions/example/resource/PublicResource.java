package com.trikorasolutions.example.resource;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;

@ApplicationScoped
@Path("/api/public")
@Produces("application/json")
@Consumes("application/json")
public class PublicResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(PublicResource.class);

  @GET
  @Path("/msg/{msg}")
  public Uni<RestResponse<String>> create(@RestPath final String msg) {
    return Uni.createFrom().item(RestResponse.ResponseBuilder.ok("Hello " +msg).build());
  }
}