package com.trikorasolutions.example.keycloak.client.bl;

import com.trikorasolutions.example.bl.KeycloakInfo;
import com.trikorasolutions.example.bl.UserLogic;
import com.trikorasolutions.example.dto.UserDto;
import com.trikorasolutions.example.keycloak.client.clientresource.KeycloakAuthAdminResource;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import java.util.List;

@ApplicationScoped
public class KeycloakClientLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakClientLogic.class);

  @RestClient
  KeycloakAuthAdminResource keycloakClient;

  public Uni<JsonArray> getUsersForGroup(String realm, SecurityIdentity keycloakSecurityContext, final String keycloakClientId, final String groupId) {
    LOGGER.info("LLEGO A LOGICA DEL CLIENTE :{}", groupId);


    return keycloakClient.getGroupUsers(
      "Bearer " + keycloakSecurityContext.getCredential(io.quarkus.oidc.AccessTokenCredential.class).getToken(), realm,
      "implicit", keycloakClientId, groupId);
  }

}
