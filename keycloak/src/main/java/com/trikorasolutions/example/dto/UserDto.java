package com.trikorasolutions.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.security.credential.Credential;
import java.util.Collection;
import java.util.Set;


public class UserDto {

  @JsonProperty("userName")
  public String userName;

  @JsonProperty("givenName")
  public String givenName;

  @JsonProperty("familyName")
  public String familyName;

  @JsonProperty("nickName")
  public String nickName;

  @JsonProperty("preferredUsername")
  public String preferredUsername;

  @JsonProperty("email")
  public String email;

  @JsonProperty("emailVerified")
  public String emailVerified;

  @JsonProperty("groups")
  public Set<String> groups;

  @JsonProperty("userPermissions")
  public Collection<Object> userPermissions;

  @JsonProperty("userCredentials")
  public Set<Credential> userCredentials;

  @JsonProperty("userRoles")
  public Set<String> userRoles;

  @JsonProperty("enabled")
  public Boolean enabled;

  public UserDto() {}

  public UserDto(String name) {
    this.userName = name;
  }

  public UserDto(String givenName, String familyName, String email, Boolean enabled, String userName) {
    this.givenName = givenName;
    this.familyName = familyName;
    this.email = email;
    this.enabled = enabled;
    this.userName = userName;
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

  public UserDto setUserPermissions(Collection<Object> userPermissions) {
    this.userPermissions = userPermissions;
    return this;
  }

  public UserDto setUserCredentials(Set<Credential> userCredentials) {
    this.userCredentials = userCredentials;
    return this;
  }

  public UserDto setUserRoles(Set<String> userRoles) {
    this.userRoles = userRoles;
    return this;
  }

  @Override
  public String toString() {
    return "UserDto{" + "userName='" + userName + '\'' + ", givenName='" + givenName + '\'' + ", familyName='" + familyName + '\'' + ", nickName='" + nickName + '\'' + ", preferredUsername='" + preferredUsername + '\'' + ", email='" + email + '\'' + ", emailVerified='" + emailVerified + '\'' + ", groups=" + groups + ", userPermissions=" + userPermissions + ", userCredentials=" + userCredentials + ", userRoles=" + userRoles + ", enabled=" + enabled + '}';
  }
}