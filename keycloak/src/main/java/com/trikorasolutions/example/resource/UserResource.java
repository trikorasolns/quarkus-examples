package com.trikorasolutions.example.resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
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
import java.io.IOException;

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
  public Uni<Response> getUserInfo() {
    return Uni.createFrom().item(Response.ok(this.keycloakSecurityContext.getPrincipal().getName()).build());
  }

  @GET
  @Path("/roles")
  @NoCache
  public Uni<Response> getRoles() throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    return Uni.createFrom().item(
      Response.ok(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.keycloakSecurityContext.getRoles()))
        .build());
  }

  @GET
  @Path("/attributes")
  @NoCache
  public Uni<Response> getAttributes() throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode user1 = mapper.createObjectNode();
    user1.put("name", this.keycloakSecurityContext.getPrincipal().getName());

    ArrayNode attr = mapper.createArrayNode();
    this.keycloakSecurityContext.getAttributes().forEach((i, j) -> attr.add(i));
    user1.set("attributes", attr);

    this.keycloakSecurityContext.getAttributes().forEach((k, v) -> LOGGER.info("k: {}, v: {}", k, v));
    LOGGER.info("configuration-metadata, issuer: {}",
      ((io.quarkus.oidc.OidcConfigurationMetadata) this.keycloakSecurityContext.getAttribute(
        "configuration-metadata")).getIssuer());

    LOGGER.info("security: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user1));
    LOGGER.info("security attributes: {}", this.keycloakSecurityContext.getAttributes());
    return Uni.createFrom().item(Response.ok(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user1)
//        "\nUSER:" + keycloakSecurityContext.getPrincipal().getName() +
//        "\nWITH ROLES " + keycloakSecurityContext.getRoles() +
//        "\nWITH CREDENTIALS " +keycloakSecurityContext.getCredentials()
    ).build());
  }


}