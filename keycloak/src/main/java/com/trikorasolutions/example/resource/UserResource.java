package com.trikorasolutions.example.resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.trikorasolutions.example.model.User;
import graphql.com.google.common.collect.Lists;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(HelloResource.class);

  @Inject
  SecurityIdentity identity;

  @Inject
  SecurityIdentity keycloakSecurityContext;

  @GET
  @Path("/userinfo")
  @NoCache
  public Uni<Response> getUserInfo() throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode user1 = mapper.createObjectNode();
    user1.put("name", this.keycloakSecurityContext.getPrincipal().getName() );

    ArrayNode attr = mapper.createArrayNode();
    this.keycloakSecurityContext.getAttributes().forEach((i,j)->attr.add(i));

    user1.set("attributes",attr);


    LOGGER.info("security: {}" , mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user1));
    LOGGER.info("security attributes: {}" , this.keycloakSecurityContext.getAttributes());
    return Uni.createFrom().item(Response.ok(
      mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user1)
//        "\nUSER:" + keycloakSecurityContext.getPrincipal().getName() +
//        "\nWITH ROLES " + keycloakSecurityContext.getRoles() +
//        "\nWITH CREDENTIALS " +keycloakSecurityContext.getCredentials()
    ).build());
  }

  @GET
  @Path("/roles")
  @NoCache
  public Uni<Response> getRoles() throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    LOGGER.info("out_roles lstRoles mapper: {}",
      mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.keycloakSecurityContext.getRoles()));


    return Uni.createFrom().item(Response.ok(
      mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.keycloakSecurityContext.getRoles()))
      .build());
  }

}