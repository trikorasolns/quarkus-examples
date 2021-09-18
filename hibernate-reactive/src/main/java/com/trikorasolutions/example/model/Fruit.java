package com.trikorasolutions.example.model;

import javax.persistence.*;

@Entity
@Table(name = "fruit")
@NamedQuery(name = "Fruit.listAll", query = "SELECT f FROM Fruit f ORDER BY f.name")
@NamedQuery(name = "Fruit.delete", query = "DELETE FROM Fruit f WHERE f.name = :name")
@NamedQuery(name = "Fruit.fetchFamily", query = "SELECT f FROM Fruit f WHERE f.family = :family")
public class Fruit {

  @Id
  @Column(length = 50, unique = true)
  public String name;

  @Column(length = 200)
  public String description;

  @Column(length = 50)
  public String family;

  @Column(nullable = false)
  public Boolean ripen = false;

//  @SequenceGenerator(name = "fruitSequence", sequenceName = "fruit_id_seq", allocationSize = 1, initialValue = 1)
//  @GeneratedValue(generator = "fruitSequence")
//  private Integer id;

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
    ripen = ripen;
  }
}