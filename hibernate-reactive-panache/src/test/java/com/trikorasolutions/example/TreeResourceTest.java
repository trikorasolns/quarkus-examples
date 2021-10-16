package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TreeResourceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeResourceTest.class);


  @Test
  public void createTreeOk() {

    given().when().body(new Tree("A tree")).contentType(MediaType.APPLICATION_JSON).post("/tree/create").then()
      .statusCode(OK.getStatusCode()).body("name", is("A tree"));
  }

  @Test
  public void createTreeWithFruitsOk() {

    final List<Fruit> fruits = new ArrayList<>(2) {{
      add(new Fruit("orange", "Updated-Pear", "PearFam", true));
      add(new Fruit("Carrot", "not a fruit", "CarrotFam", true));
    }};

    Tree tree = new Tree("AnotherTree");

    tree.setTreeFruits(fruits);

    // Ensure the tree is persisted
    given().when().body(tree).contentType(MediaType.APPLICATION_JSON).post("/tree/create").then()
      .statusCode(OK.getStatusCode()).body("name", is("AnotherTree"),
        "treeFruits.name[0]", is("orange"),
        "treeFruits.name[1]", is("Carrot"));

    // Ensure that the fruits persisted when persisting the tree are in th db
    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode())
      .body("name",hasItems("orange", "pear"));
  }


}