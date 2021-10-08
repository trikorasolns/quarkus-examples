package com.trikorasolutions.example.keycloak.dto;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Set;


public class UserRepresentation {
  /**
   * In this first version of the example, the credential of the users are
   * their usernames. This feature will be enhanced in future releases.
   */
  public class UserDtoCredential {
    @JsonbProperty("type")
    public String type;

    @JsonbProperty("value")
    public String value;

    @JsonbProperty("temporary")
    public Boolean temporary;

    public UserDtoCredential(String name){
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

  public UserRepresentation(String firstName, String lastName, String email, Boolean enabled, String username) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.enabled = enabled;
    this.username = username;
    this.credentials = Set.of(this.new UserDtoCredential(username));
  }

  public UserRepresentation(UserRepresentation newUser) {
    this.firstName = newUser.firstName;
    this.lastName = newUser.lastName;
    this.email = newUser.email;
    this.enabled = newUser.enabled;
    this.username = newUser.username;
    this.credentials = Set.of(this.new UserDtoCredential(newUser.username));
  }

}
