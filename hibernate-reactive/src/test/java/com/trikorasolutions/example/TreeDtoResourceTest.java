package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
public class TreeDtoResourceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeDtoResourceTest.class);

  @Inject
  Mutiny.SessionFactory sf;

  @BeforeEach
  public void clearDatabase() {

    LOGGER.warn("delete from database");
    Integer res = sf.withTransaction((s, t) -> s.createQuery("DELETE FROM Fruit").executeUpdate()).await()
      .atMost(Duration.ofSeconds(30));
    Integer res2 = sf.withTransaction((s, t) -> s.createQuery("DELETE FROM Tree").executeUpdate()).await()
      .atMost(Duration.ofSeconds(30));
    LOGGER.warn("{} records removed", res+res2);

  }
  @Test
  public void createTreeOk() {

    given().when().body(new Tree("A tree", new Fruit("strawberry", "Strawberry", "Berry", false)))
      .contentType(MediaType.APPLICATION_JSON).post("/tree/create").then()
      .statusCode(OK.getStatusCode());//.body("name", is("A tree"));

    // Is the tree persisted
    LOGGER.info("PERSISTED TREES: {}",
      given().when().get("/tree/listAll").then().statusCode(OK.getStatusCode()).extract().response().prettyPrint());
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
        "treeFruits.name[1]", is("orange"),
        "treeFruits.name[0]", is("Carrot"));


    // Ensure that the fruits persisted when persisting the tree are in th db
    LOGGER.info("PERSISTED FRUIT: {}",
    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode())
      .extract().response().prettyPrint());

    LOGGER.info("PERSISTED TREES: {}",
      given().when().get("/tree/listAll").then().statusCode(OK.getStatusCode()).extract().response().prettyPrint());

  }

}