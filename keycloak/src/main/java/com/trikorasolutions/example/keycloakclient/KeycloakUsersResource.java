package com.trikorasolutions.example.keycloakclient;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonObject;
import javax.ws.rs.*;

@Path("/auth")
@RegisterRestClient(configKey = "keycloak-api")
public interface KeycloakUsersResource {

  @PUT
  @Path("/realms/{realm}/protocol/openid-connect/userinfo")
  @Produces("application/json")
  Uni<JsonObject> add(@PathParam("realm") String realm);

  @GET
  @Path("/realms/{realm}/protocol/openid-connect/userinfo")
  @Produces("application/json")
  Uni<JsonObject> listAll(@PathParam("realm") String realm, @QueryParam("grant_type") String grantType, @QueryParam("client_id") String clientId, @QueryParam("client_secret") String clientSecret);

//  @Path("users")
//  UsersResource users();

}
