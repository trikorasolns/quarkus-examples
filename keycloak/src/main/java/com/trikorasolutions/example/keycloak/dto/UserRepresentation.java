package com.trikorasolutions.example.keycloak.dto;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class UserRepresentation {
  @JsonbProperty("firstName")
  public String firstName;

  @JsonbProperty("lastName")
  public String lastName;

  @JsonbProperty("email")
  public String email;

  @JsonbProperty("enabled")
  public Boolean enabled;

  @JsonbProperty("username")
  public String username;

  @JsonbCreator
  public UserRepresentation(@JsonbProperty("firstName")String firstName,
                            @JsonbProperty("lastName") String lastName,
                            @JsonbProperty("email") String email,
                            @JsonbProperty("enabled") Boolean enabled,
                            @JsonbProperty("username") String username) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.enabled = enabled;
    this.username = username;
  }

  @JsonbCreator
  public UserRepresentation(UserRepresentation newUser) {
    this.firstName = newUser.firstName;
    this.lastName = newUser.lastName;
    this.email = newUser.email;
    this.enabled = newUser.enabled;
    this.username = newUser.username;
  }

  @Override
  public String toString() {
    return "{" + "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\'' + ", enabled='" + enabled + '\'' + ", username='" + username + '\'' + '}';
  }
}
