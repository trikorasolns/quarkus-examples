package com.trikorasolutions.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(name = "Tree")
@Table(name = "tree")
public class Tree extends PanacheEntityBase {
  @Id
  @Column(length = 50, unique = true)
  public String name;

  @OneToMany(
    mappedBy = "tree",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<Fruit> treeFruits;

  public void addFruit(Fruit fruit) {
    treeFruits.add(fruit);
    fruit.setTree(this);
  }

  public void addFruits(List<Fruit> fruits) {
    treeFruits.addAll(fruits);
    fruits.stream().forEach(fruit->fruit.setTree(this));
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