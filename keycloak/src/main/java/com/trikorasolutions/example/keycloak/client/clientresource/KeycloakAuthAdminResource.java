package com.trikorasolutions.example.keycloak.client.clientresource;

import com.trikorasolutions.example.keycloak.client.dto.UserRepresentation;
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

  @PUT
  @Path("/realms/{realm}/users/{id}")
  @Produces("application/json")
  Uni<JsonArray> updateUser(@HeaderParam("Authorization") String bearerToken,
                            @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                            @QueryParam("client_id") String clientId, @PathParam("id") String id,
                            UserRepresentation body);

  @GET
  @Path("/realms/{realm}/users")
  @Produces("application/json")
  Uni<JsonArray> getUserInfo(@HeaderParam("Authorization") String bearerToken,
                             @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                             @QueryParam("client_id") String clientId, @QueryParam("username") String username);

  @DELETE
  @Path("/realms/{realm}/users/{id}")
  @Produces("application/json")
  Uni<JsonArray> deleteUser(@HeaderParam("Authorization") String bearerToken,
                             @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                             @QueryParam("client_id") String clientId, @PathParam("id") String id);

  @GET
  @Path("/realms/{realm}/groups")
  @Produces("application/json")
  Uni<JsonArray> getGroupInfo(@HeaderParam("Authorization") String bearerToken,
                             @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                             @QueryParam("client_id") String clientId, @QueryParam("search") String groupName);

  @GET
  @Path("/{realm}/groups/{id}/members")
  @Produces("application/json")
  Uni<JsonArray> getGroupUsers(@HeaderParam("Authorization") String bearerToken,
                              @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                              @QueryParam("client_id") String clientId, @PathParam("id") String id);

}
