package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class FruitReactiveResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FruitReactiveResourceTest.class);

  @Inject
  Mutiny.SessionFactory sf;

  @BeforeEach
  public void clearDatabase() {
    LOGGER.warn("delete from database");
    Integer res = sf.withTransaction((s, t) -> s.createQuery("DELETE FROM Fruit").executeUpdate()).await()
      .atMost(Duration.ofSeconds(30));
    LOGGER.warn("{} records removed", res);
  }

  @Test
  public void testGetFruitReactiveUnknown() {
    given().when().get("/fruitreact/name/unknown").then().statusCode(NOT_FOUND.getStatusCode());
  }

  @Test
  public void testGetFruitPearReactive() {
    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(200)
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""))
    ;

    given().when().body("{\"name\": \"pear\", \"description\": \"Pear\"}").contentType("application/json")
      .post("/fruitreact/create").then().statusCode(200)
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""))
    ;

    given().when().get("/fruitreact/name/pear").then().statusCode(200)
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""))
    ;

    given().when().delete("/fruitreact/pear").then().statusCode(200).body(containsString("1"));

    given().when().get("/fruitreact/name/pear").then().statusCode(NOT_FOUND.getStatusCode());
  }

//  @Test
  public void testFruitListReactive() {
//    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType("application/json")
//      .post("/fruitreact/create").then().statusCode(200)
//      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""));
//
//    given().when().body(new Fruit("apple", "Apple", "Rosaceae", false)).contentType("application/json")
//      .post("/fruitreact/create").then().statusCode(200)
//      .body(containsString("\"name\":\"apple\""), containsString("\"description\":\"Apple\""));

//    given().when().get("/fruitreact/listAll").then().statusCode(200)
//      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""),
//        containsString("\"name\":\"apple\""), containsString("\"description\":\"Apple\""));
//
//    given().when().delete("/fruitreact/lemon").then().statusCode(NOT_FOUND.getStatusCode());
  }

//  @Test
//  public void testFruitRipeReactive() {
//    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType("application/json")
//      .post("/fruitreact/create").then().statusCode(200)
//      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""));
//
//    given().when().body(new Fruit("apple", "Apple", "Rosaceae", false)).contentType("application/json")
//      .post("/fruitreact/create").then().statusCode(200)
//      .body(containsString("\"name\":\"apple\""), containsString("\"description\":\"Apple\""));
//
//    given().when().body(new Fruit("pineapple", "Pineapple", "Bromeliaceae", false)).contentType("application/json")
//      .post("/fruitreact/create").then().statusCode(200)
//      .body(containsString("\"name\":\"pineapple\""), containsString("\"description\":\"Pineapple\""));
//
//    given().when().body(new Fruit("lemon", "Lemon", "Rutaceae", false)).contentType("application/json")
//      .post("/fruitreact/create").then().statusCode(200)
//      .body(containsString("\"name\":\"lemon\""), containsString("\"description\":\"Lemon\""));
//
//    given().when().put("/fruitreact/ripe/Rosaceae").then().statusCode(200);
//
//    given().when().get("/fruitreact/name/pear").then().statusCode(200)
//      .body(containsString("\"name\":\"pear\""), containsString("\"ripen\":true"));
//
//    given().when().get("/fruitreact/name/apple").then().statusCode(200)
//      .body(containsString("\"name\":\"apple\""), containsString("\"ripen\":true"));
//
//    given().when().get("/fruitreact/name/lemon").then().statusCode(200)
//      .body(containsString("\"name\":\"lemon\""), containsString("\"ripen\":false"));
//
//    given().when().get("/fruitreact/name/pineapple").then().statusCode(200)
//      .body(containsString("\"name\":\"pineapple\""), containsString("\"ripen\":false"));
//
//  }


}