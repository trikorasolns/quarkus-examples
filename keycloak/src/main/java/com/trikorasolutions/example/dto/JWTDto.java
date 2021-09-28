package com.trikorasolutions.example.dto;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.List;
import java.util.Set;


public class JWTDto {
  @JsonbProperty("userName")
  public String userName;

  @JsonbProperty("userAttributes")
  public List<String> userAttributes;

  @JsonbProperty("userPermissions")
  public List<String> userPermissions;

  @JsonbProperty("userCredentials")
  public List<String> userCredentials;

  @JsonbProperty("userRoles")
  public Set<String> userRoles;

  @JsonbCreator
  public JWTDto(final String name) {
    this.userName = name;

  }

  //public static UserDto build(){return UserDto ;};

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setUserAttributes(List<String> userAttributes) {
    this.userAttributes = userAttributes;
  }

  public void setUserPermissions(List<String> userPermissions) {
    this.userPermissions = userPermissions;
  }

  public void setUserCredentials(List<String> userCredentials) {
    this.userCredentials = userCredentials;
  }

  public void setUserRoles(Set<String> userRoles) {
    this.userRoles = userRoles;
  }
}