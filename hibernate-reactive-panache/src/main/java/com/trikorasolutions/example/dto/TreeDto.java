package com.trikorasolutions.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;

import java.util.List;
import java.util.stream.Collectors;

public class TreeDto {

  @JsonProperty("name")
  public String name;

  @JsonProperty("fruits")
  public List<FruitDto> fruits;

  public TreeDto() {
  }

//  public TreeDto(String name,List<String> lst) {
//    this.fruits = lst;
//    this.name = name;
//  }

  public TreeDto(String name) {
    this.name = name;
  }

  public TreeDto(String name, List<FruitDto> fruits) {
    this.name = name;
    this.fruits = fruits;
  }

  public static TreeDto from(Tree tree) {
    List<FruitDto> lst = tree.getTreeFruits().stream().map(FruitDto::from).collect(Collectors.toList());
    return new TreeDto(tree.name, lst);
  }
}