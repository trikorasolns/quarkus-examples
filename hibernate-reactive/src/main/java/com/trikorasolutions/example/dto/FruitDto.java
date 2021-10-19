package com.trikorasolutions.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trikorasolutions.example.model.Fruit;

public class FruitDto {
  @JsonProperty("name")
  public String name;

  @JsonProperty("description")
  public String description;

  @JsonProperty("family")
  public String family;

  @JsonProperty("ripen")
  public Boolean ripen = false;


  public FruitDto(String name, String description, String family, Boolean ripen) {
    this.name = name;
    this.description = description;
    this.family = family;
    this.ripen = ripen;
  }

  public static FruitDto from(Fruit f){
    return new FruitDto(f.name,f.description, f.family,f.ripen);
  }
}