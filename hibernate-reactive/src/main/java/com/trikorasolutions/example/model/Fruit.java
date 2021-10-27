package com.trikorasolutions.example.model;
import javax.persistence.*;

@Entity
@Table(name = "fruit")
@NamedQuery(name = "Fruit.listAll", query = "SELECT f FROM Fruit f ORDER BY f.name")
@NamedQuery(name = "Fruit.delete", query = "DELETE FROM Fruit f WHERE f.name = :name")
@NamedQuery(name = "Fruit.fetchFamily", query = "SELECT f FROM Fruit f WHERE f.family = :family")
@NamedQuery(name = "Fruit.fetchByTree", query = "SELECT f FROM Fruit f WHERE f.tree = :tree")
public class Fruit {
  @Id
  @Column(length = 50, unique = true, name = "name")
  public String name;

  @Column(length = 200, name = "description")
  public String description;

  @Column(length = 50, name = "family")
  public String family;

  @Column(nullable = false, name = "ripen")
  public Boolean ripen = false;

  @Column(length = 50, name = "tree")
  public String tree;

  public Fruit() {
  }

  public Fruit(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Fruit(String name, String description, String family, Boolean isRipen) {
    this.name = name;
    this.description = description;
    this.family = family;
    this.ripen = isRipen;
  }

  public Fruit(String name, String description, String family, Boolean isRipen, final String tree) {
    this.name = name;
    this.description = description;
    this.family = family;
    this.ripen = isRipen;
    this.tree = tree;
  }

  @Override
  public String toString() {
    return "Fruit{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", family='" + family + '\'' + ", ripen=" + ripen + ", tree='" + tree + '\'' + '}';
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public Boolean getRipen() {
    return ripen;
  }

  public void setRipen(Boolean ripen) {
    this.ripen = ripen;
  }
}