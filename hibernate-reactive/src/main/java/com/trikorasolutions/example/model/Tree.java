package com.trikorasolutions.example.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Tree")
@Table(name = "tree")
@NamedQuery(name = "Tree.listAll", query = "SELECT t FROM Tree t ORDER BY t.name")
@NamedQuery(name = "Tree.delete", query = "DELETE FROM Tree t WHERE t.name = :name")
public class Tree {

  @Id
  @Column(length = 50, unique = true)
  public String name;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "ownerTree", orphanRemoval = true)
  private List<Fruit> treeFruits;

//  public void addFruits(Fruit fruit) {
//    treeFruits.add(fruit);
//  }
//
//  public void addFruits(List<Fruit> fruits) {
//    treeFruits.addAll(fruits);
//    fruits.stream().forEach(f -> f.setOwner(this));
//  }

  public Tree() {
  }

  public Tree(String name) {
    this.name = name;
  }

  public Tree(String name, List<Fruit> fruits) {
    this.name = name;
    this.treeFruits = fruits;
  }

  public Tree(String name, Fruit f) {
    this.name = name;
    this.treeFruits = new ArrayList<Fruit>();
    this.treeFruits.add(f);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Tree{" + "name='" + name + '\'' + ", treeFruits=" + treeFruits + '}';
  }

  public void setTreeFruits(List<Fruit> treeFruits) {
    this.treeFruits = treeFruits;
  }

  public List<Fruit> getTreeFruits() {
    return this.treeFruits;
  }

}