package com.trikorasolutions.example.dto;

import io.quarkus.security.credential.Credential;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Collection;
import java.util.Set;


public class UserDto {
  @JsonbProperty("userName")
  public String userName;

  @JsonbProperty("givenName")
  public String givenName;

  @JsonbProperty("familyName")
  public String familyName;

  @JsonbProperty("nickName")
  public String nickName;

  @JsonbProperty("preferredUsername")
  public String preferredUsername;

  @JsonbProperty("email")
  public String email;

  @JsonbProperty("emailVerified")
  public String emailVerified;

  @JsonbProperty("groups")
  public Set<String> groups;

  @JsonbProperty("userPermissions")
  public Collection<Object> userPermissions;

  @JsonbProperty("userCredentials")
  public Set<Credential>  userCredentials;

  @JsonbProperty("userRoles")
  public Set<String> userRoles;

  @JsonbCreator
  public UserDto(@JsonbProperty("userName") String name) {
    this.userName = name;
  }

  public UserDto setUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public UserDto setGivenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  public UserDto setFamilyName(String familyName) {
    this.familyName = familyName;
    return this;
  }

  public UserDto setNickName(String nickName) {
    this.nickName = nickName;
    return this;
  }

  public UserDto setPreferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
    return this;
  }

  public UserDto setEmail(String email) {
    this.email = email;
    return this;
  }

  public UserDto setEmailVerified(String emailVerified) {
    this.emailVerified = emailVerified;
    return this;
  }

  public UserDto setGroups(Set<String> groups) {
    this.groups = groups;
    return this;
  }

  public UserDto setUserPermissions(Collection<Object>  userPermissions) {
    this.userPermissions = userPermissions;
    return this;
  }

  public UserDto setUserCredentials(Set<Credential>  userCredentials) {
    this.userCredentials = userCredentials;
    return this;
  }

  public UserDto setUserRoles(Set<String> userRoles) {
    this.userRoles = userRoles;
    return this;
  }
}