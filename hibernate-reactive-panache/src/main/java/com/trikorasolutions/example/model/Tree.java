package com.trikorasolutions.example.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Tree")
@Table(name = "tree")
@ApplicationScoped
public class Tree extends PanacheEntityBase {
  @Id
  @Column(length = 50, unique = true)
  public String name;


  @OneToMany(
    mappedBy = "tree",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
//  @JoinColumn(name="tree_id")
  private List<Fruit> treeFruits;

  public void addFruit(Fruit fruit) {
    treeFruits.add(fruit);
    fruit.setTree(this);
  }

  public void removeComment(Fruit fruit) {
    treeFruits.remove(fruit);
    fruit.setTree(null);
  }

  public Tree() {
  }

  public Tree(String name) {
    this.name = name;
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