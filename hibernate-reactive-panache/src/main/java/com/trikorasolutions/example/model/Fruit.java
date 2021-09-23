package com.trikorasolutions.example.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fruit")
@ApplicationScoped
public class Fruit extends PanacheEntityBase {
  @Id
  @Column(length = 50, unique = true)
  public String name;

  @Column(length = 200)
  public String description;

  @Column(length = 50)
  public String family;

  @Column(nullable = false)
  public Boolean ripen = false;

  public Fruit() {
  }

  public Fruit(String name) {
    this.name = name;
    this.description = name;
  }

  public Fruit(String name, String description) {
    this(name);
    this.description = description;
  }

  public Fruit(String name, String description, String family, Boolean isRipen) {
    this(name, description);
    this.family = family;
    this.ripen = isRipen;
  }

  @Override
  public String toString() {
    return "Fruit{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", family='" + family + '\'' + ", ripen=" + ripen + '}';
  }

  public String getDescription() {return description;}

  public void setDescription(String description) {this.description = description;}

  public String getFamily() {return family;}

  public void setFamily(String family) {this.family = family;}

  public Boolean getRipen() {return ripen;}

  public void setRipen(Boolean ripen) {this.ripen = ripen;}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}