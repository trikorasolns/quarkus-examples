package com.trikorasolutions.example.keycloakclient;

import io.restassured.RestAssured;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonObject;
import javax.ws.rs.*;

@Path("/auth")
@RegisterRestClient(configKey = "keycloak-api")
public interface KeycloakAuthorizationResource {

  @GET
  @Path("/admin/realms/gateway/users")
  @Produces("application/json")
  Uni<JsonObject> listAll(@HeaderParam("Authorization") String bearerToken,
                          @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                          @QueryParam("client_id") String clientId);

  @GET
  @Path("/realms/{realm}/protocol/openid-connect/userinfo")
  @Produces("application/json")
  Uni<JsonObject> userInfo(@HeaderParam("Authorization") String bearerToken,
                          @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                          @QueryParam("client_id") String clientId);
}
