package com.trikorasolutions.example.keycloak.dto;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Set;
import java.util.TreeSet;

public class UserRepresentation {
  public class UserDtoCredential {
    @JsonbProperty("type")
    public String type;

    @JsonbProperty("value")
    public String value;

    @JsonbProperty("temporary")
    public Boolean temporary;

    @JsonbCreator
    public UserDtoCredential(@JsonbProperty("value") String name){
      this.value = name;
      this.type = "password";
      this.temporary = false;
    }

  }

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

  @JsonbProperty("credentials")
  public Set<UserDtoCredential> credentials;

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
    this.credentials = Set.of(this.new UserDtoCredential(username));
  }

  @JsonbCreator
  public UserRepresentation(UserRepresentation newUser) {
    this.firstName = newUser.firstName;
    this.lastName = newUser.lastName;
    this.email = newUser.email;
    this.enabled = newUser.enabled;
    this.username = newUser.username;
    this.credentials = Set.of(this.new UserDtoCredential(newUser.username));
  }

}
