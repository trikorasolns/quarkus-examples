package com.trikorasolutions.example.model;

import io.quarkus.security.identity.SecurityIdentity;


public class User {
  private final SecurityIdentity secContext;

  public User(SecurityIdentity securityContext) {
    this.secContext = securityContext;
  }

  public String getUserName() {
    return this.secContext.getPrincipal().getName();
  }

  public SecurityIdentity getUserIdentity() {
    return this.secContext;
  }


}