package com.trikorasolutions.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trikorasolutions.example.model.Fruit;

import java.util.List;
import java.util.stream.Collectors;

public class FruitDto {
  @JsonProperty(value = "name", required = true)
  public String name;

  @JsonProperty("description")
  public String description;

  @JsonProperty("family")
  public String family;

  @JsonProperty("ripen")
  public Boolean ripen = false;

  @JsonProperty("tree")
  public String tree;

  public FruitDto() {
  }

  public FruitDto(String name, String description, String family, Boolean ripen) {
    this.name = name;
    this.description = description;
    this.family = family;
    this.ripen = ripen;
  }

  public FruitDto(String name, String description, String family, Boolean ripen, final String tree) {
    this.name = name;
    this.description = description;
    this.family = family;
    this.ripen = ripen;
    this.tree = tree;
  }

  public static FruitDto from(Fruit f) {
    return new FruitDto(f.name, f.description, f.family, f.ripen, f.tree);
  }

  public static List<FruitDto> allFrom(List<Fruit> fruits) {
    return fruits.stream().map(FruitDto::from).collect(Collectors.toList());
  }

  public Fruit toFruit() {
    return new Fruit(this.name,this.description, this.family, this.ripen, this.tree);
  }

  @Override
  public String toString() {
    return "FruitDto{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", family='" + family + '\'' + ", ripen=" + ripen + ", tree='" + tree + '\'' + '}';
  }
}