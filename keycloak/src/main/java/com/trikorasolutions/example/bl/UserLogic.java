package com.trikorasolutions.example.bl;

import com.trikorasolutions.example.dto.UserDto;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

public class UserLogic  {

  //TODO: Implement this method through a functional interface
  public static UserDto load(SecurityIdentity identity, JsonWebToken jwt) {

    UserDto user = new UserDto(identity.getPrincipal().getName());
    return user.setUserRoles(identity.getRoles())
      .setUserPermissions(identity.getAttribute("permissions"))
      .setUserCredentials(identity.getCredentials())
      .setUserName(identity.getPrincipal().getName())
      .setEmail(jwt.getClaim("email"))
      .setEmailVerified(jwt.getClaim("email_verified").toString())
      .setGivenName(jwt.getClaim("given_name"))
      .setPreferredUsername(jwt.getClaim("preferred_username"))
      .setFamilyName(jwt.getClaim("family_name"))
      .setGroups(jwt.getClaim("groups"))
    ;
  }
}