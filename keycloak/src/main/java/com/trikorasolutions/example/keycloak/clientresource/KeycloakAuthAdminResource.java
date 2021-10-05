package com.trikorasolutions.example.keycloak.clientresource;

import com.trikorasolutions.example.keycloak.dto.UserRepresentation;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonArray;
import javax.ws.rs.*;

@Path("/auth/admin")
@RegisterRestClient(configKey = "keycloak-api")
public interface KeycloakAuthAdminResource {

  @GET
  @Path("/realms/{realm}/users")
  @Produces("application/json")
  Uni<JsonArray> listAll(@HeaderParam("Authorization") String bearerToken,
                         @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                         @QueryParam("client_id") String clientId);

  @POST
  @Path("/realms/{realm}/users")
  @Produces("application/json")
  Uni<JsonArray> createUser(@HeaderParam("Authorization") String bearerToken,
                         @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                         @QueryParam("client_id") String clientId, UserRepresentation body);

}
