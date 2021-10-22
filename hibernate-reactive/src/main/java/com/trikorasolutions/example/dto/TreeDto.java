package com.trikorasolutions.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeDto {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeDto.class);

  @JsonProperty(value = "name", required = true)
  public String name;

  @JsonProperty("fruits")
  public List<FruitDto> fruits;

  public TreeDto() {
  }

  public TreeDto(String name) {
    this.name = name;
  }

  public TreeDto(String name, List<FruitDto> fruits) {
    try {
      this.name = name;
      if (fruits != null && fruits.size() > 0) {
        fruits.forEach(fruit -> fruit.tree = name);
      }
      this.fruits = fruits;
    } catch (RuntimeException ex) {
      LOGGER.error(ex.getMessage());
      LOGGER.error(Arrays.toString(ex.getStackTrace()));
    }
  }

  public static TreeDto from(Tree tree) {
    if (tree == null) {
      return null;
    } else {
      return new TreeDto(tree.name, null);
    }
  }

  public Tree toTree() {
    if (fruits != null && fruits.size() > 0) {
      List<Fruit> fruitsEntity = new ArrayList<>(fruits.size());
      fruits.forEach(fruitDto -> fruitsEntity.add(fruitDto.toFruit()));
      return new Tree(name, fruitsEntity);
    } else {
      return new Tree(name);
    }
  }

  public List<FruitDto> getFruits() {
    return fruits;
  }

  public void setFruits(List<FruitDto> fruits) {
    this.fruits = fruits;
  }

  @Override
  public String toString() {
    return "TreeDto{" + "name='" + name + '\'' + ", fruits=" + fruits + '}';
  }
}