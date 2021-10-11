package com.trikorasolutions.example.keycloak.client.clientresource;

import com.trikorasolutions.example.keycloak.client.dto.UserRepresentation;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonArray;
import javax.ws.rs.*;


@Path("/auth/admin")
@RegisterRestClient(configKey = "keycloak-api")
public interface KeycloakAuthAdminResource {

  /**
   * This method return a list with all the user in the client provided as argument
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @return a JsonArray of Keycloak UserRepresentations.
   */
  @GET
  @Path("/realms/{realm}/users")
  @Produces("application/json")
  Uni<JsonArray> listAll(@HeaderParam("Authorization") String bearerToken,
                         @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                         @QueryParam("client_id") String clientId);

  /**
   * Register a new user in the Keycloak client.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param body raw string containing the new user in the UserRepresentation format.
   * @return A JsonArray with the UserRepresentation of the created user.
   */
  @POST
  @Path("/realms/{realm}/users")
  @Produces("application/json")
  Uni<JsonArray> createUser(@HeaderParam("Authorization") String bearerToken,
                           @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                           @QueryParam("client_id") String clientId, UserRepresentation body);

  /**
   * Updated a user in Keycloak.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param id Id of the user that is going to be updated.
   * @param body Raw string containing the new user data in the UserRepresentation format.
   * @return A UserRepresentation of the updated user.
   */
  @PUT
  @Path("/realms/{realm}/users/{id}")
  @Produces("application/json")
  Uni<JsonArray> updateUser(@HeaderParam("Authorization") String bearerToken,
                            @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                            @QueryParam("client_id") String clientId, @PathParam("id") String id,
                            UserRepresentation body);

  /**
   * Return the UserRepresentation of one user queried by his username.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param username username of the user witch is going to be searched.
   * @return a UserRepresentation of the user.
   */
  @GET
  @Path("/realms/{realm}/users")
  @Produces("application/json")
  Uni<JsonArray> getUserInfo(@HeaderParam("Authorization") String bearerToken,
                             @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                             @QueryParam("client_id") String clientId, @QueryParam("username") String username);

  /**
   * Deletes a user from the Keycloak database.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param id id of the user that is going to be deleted from the keycloak database.
   * @return a response with body equals to: "success".
   */
  @DELETE
  @Path("/realms/{realm}/users/{id}")
  @Produces("application/json")
  Uni<JsonArray> deleteUser(@HeaderParam("Authorization") String bearerToken,
                             @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                             @QueryParam("client_id") String clientId, @PathParam("id") String id);

  /**
   * Return information of one group.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param groupName name of the group that is going to be queried in the Keycloak database.
   * @return a GroupRepresentation of the desired group.
   */
  @GET
  @Path("/realms/{realm}/groups")
  @Produces("application/json")
  Uni<JsonArray> getGroupInfo(@HeaderParam("Authorization") String bearerToken,
                             @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                             @QueryParam("client_id") String clientId, @QueryParam("search") String groupName);

  /**
   * Gets all the users that belongs to a concrete group.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param id id of the group that is going to be queried.
   * @return a JsonArray of UserRepresentation.
   */
  @GET
  @Path("/realms/{realm}/groups/{id}/members")
  @Produces("application/json")
  Uni<JsonArray> getGroupUsers(@HeaderParam("Authorization") String bearerToken,
                              @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                              @QueryParam("client_id") String clientId, @PathParam("id") String id);

  /**
   * Add a user to a group.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param id id of the user that is going to be added.
   * @param groupId id of the group where the user will belong to.
   * @return void.
   */
  @PUT
  @Path("/realms/{realm}/users/{id}/groups/{groupId}")
  @Produces("application/json")
  Uni<JsonArray> putUserInGroup(@HeaderParam("Authorization") String bearerToken,
                               @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                               @QueryParam("client_id") String clientId, @PathParam("id") String id,
                               @PathParam("groupId") String groupId);

  /**
   * Removes a user from a group.
   * @param bearerToken access token provided by the  keycloak SecurityIdentity.
   * @param realm the realm name in which the users are going to be queried.
   * @param grantType kind of authentication method.
   * @param clientId id of the client (service name).
   * @param id id of the user that is going to be removed.
   * @param groupId id of the group.
   * @return void.
   */
  @DELETE
  @Path("/realms/{realm}/users/{id}/groups/{groupId}")
  @Produces("application/json")
  Uni<JsonArray> deleteUserFromGroup(@HeaderParam("Authorization") String bearerToken,
                                @PathParam("realm") String realm, @QueryParam("grant_type") String grantType,
                                @QueryParam("client_id") String clientId, @PathParam("id") String id,
                                @PathParam("groupId") String groupId);
}
