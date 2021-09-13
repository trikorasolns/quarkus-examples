package com.trikorasolutions.example.model;

import javax.persistence.*;

@Entity
@Table(name = "fruit")
@NamedQuery(name = "Fruit.listAll", query = "SELECT f FROM Fruit f ORDER BY f.name")
@NamedQuery(name = "Fruit.delete", query = "DELETE FROM Fruit f WHERE f.name = :name")
public class Fruit {

  @Id
  @Column(length = 50, unique = true)
  public String name;

  @Column(length = 200)
  public String description;


  @Column(nullable = false)
  public Boolean isRipen = false;

//  @SequenceGenerator(name = "fruitSequence", sequenceName = "fruit_id_seq", allocationSize = 1, initialValue = 1)
//  @GeneratedValue(generator = "fruitSequence")
//  private Integer id;

  public Fruit() {
  }

  public Fruit(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Fruit(String name, String description, Boolean isRipen) {
    this.name = name;
    this.description = description;
    this.isRipen = isRipen;
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


  public Boolean getRipen() {
    return isRipen;
  }

  public void setRipen(Boolean ripen) {
    isRipen = ripen;
  }
}