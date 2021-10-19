package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class CombineTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CombineTest.class);

  @Inject
  Mutiny.SessionFactory sf;

  @BeforeEach
  public void initDatabase() {
    RestAssured.defaultParser = Parser.JSON;
    LOGGER.warn("delete from database");
    Integer res = sf.withTransaction((s, t) -> s.createQuery("DELETE FROM Fruit").executeUpdate()).await()
      .atMost(Duration.ofSeconds(30));
    LOGGER.warn("{} records removed", res);

    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body("name", containsString("pear"), "description", containsString("Pear"));

    given().when().body(new Fruit("apple", "Apple", "Rosaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body("name", containsString("apple"), "description", containsString("Apple"));
  }



  @Test
  public void testCombine() {
    given().when().body(new Fruit("lemon", "Lemon", "Rutaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode());

    given().when().body(new Fruit("pineapple", "Pineapple", "Rutaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode());

//    LOGGER.info("RESPONSE COMBINE:{}",given()
//      .param("family1","Rosaceae")
//      .param("family2","Rutaceae")
//      .when()
//      .contentType(MediaType.APPLICATION_JSON)
//      .post("/tree/combine")
//      .then().statusCode(OK.getStatusCode())
//      .extract().response().prettyPrint());

        LOGGER.info("RESPONSE COMBINE:{}",given().when()
      .param("family1","Rosaceae")
      .param("family2","Rutaceae")
          .contentType(MediaType.APPLICATION_JSON)
      .post("/tree/combine")
      .then().statusCode(OK.getStatusCode())
      .extract().response().prettyPrint());


//    given().when().get("/fruitreact/combine").then().statusCode(OK.getStatusCode());
    LOGGER.info("RESPONSE LIST FRUITS:{}",
    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode()).extract().response().prettyPrint());

    LOGGER.info("RESPONSE LIST TREE:{}",
      given().when().get("/tree/name/combine_tree").then().statusCode(OK.getStatusCode()).extract().response().prettyPrint());
  }



}
