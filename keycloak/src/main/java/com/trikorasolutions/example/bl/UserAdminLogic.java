package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloakclient.KeycloakAuthAdminResource;
import com.trikorasolutions.example.keycloakclient.KeycloakAuthorizationResource;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;

@ApplicationScoped
public class UserAdminLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminLogic.class);

  @RestClient
  KeycloakAuthAdminResource keycloakClient;


  public Uni<JsonArray>  listAll(final String realm, final SecurityIdentity keycloakSecurityContext) {

    return keycloakClient.listAll(
        "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(),
        realm, "implicit", KeycloakInfo.KEYCLOAK_CLIENT_ID);
  }
}
