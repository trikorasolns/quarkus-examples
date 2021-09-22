package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class FruitReactiveResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FruitReactiveResourceTest.class);

  @Inject
  Mutiny.SessionFactory sf;

  @AfterEach
  public void tearDown(){
    LOGGER.warn("delete from database");
    Integer res = sf.withTransaction((s, t) -> s.createQuery("DELETE FROM Fruit").executeUpdate()).await()
      .atMost(Duration.ofSeconds(30));
    LOGGER.warn("{} records removed", res);

  }
  @BeforeEach
  public void clearDatabase() {
    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""));

    given().when().body(new Fruit("apple", "Apple", "Rosaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"apple\""), containsString("\"description\":\"Apple\""));
  }

  @Test
  public void testCreate() {
    given().when().get("/fruitreact/name/pear").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""));

    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(CONFLICT.getStatusCode());
  }

  @Test
  public void testGet() {
    given().when().get("/fruitreact/name/unknown").then().statusCode(NOT_FOUND.getStatusCode());

    given().when().get("/fruitreact/name/pear").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""));
  }

  @Test
  public void testListAll() {
    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pear\""), containsString("\"description\":\"Pear\""),
        containsString("\"name\":\"apple\""), containsString("\"description\":\"Apple\""));

    given().when().delete("/fruitreact/pear").then().statusCode(OK.getStatusCode());
    given().when().delete("/fruitreact/apple").then().statusCode(OK.getStatusCode());

    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode())
      .body(containsString("[]"));
  }

  @Test
  public void testDelete() {
    given().when().delete("/fruitreact/lemon").then().statusCode(NOT_FOUND.getStatusCode());

    given().when().delete("/fruitreact/pear").then().statusCode(OK.getStatusCode()).body(containsString("1"));

    given().when().get("/fruitreact/name/pear").then().statusCode(NOT_FOUND.getStatusCode());
  }

  @Test
  public void testUpdate() {
    given().when().body(new Fruit("pineapple", "Pineapple", "Bromeliaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pineapple\""), containsString("\"description\":\"Pineapple\""));

    given().when().body(new Fruit("lemon", "Lemon", "Rutaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"lemon\""), containsString("\"description\":\"Lemon\""));

    given().when().put("/fruitreact/ripe/Rosaceae").then().statusCode(OK.getStatusCode());

    given().when().get("/fruitreact/name/pear").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pear\""), containsString("\"ripen\":true"));

    given().when().get("/fruitreact/name/apple").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"apple\""), containsString("\"ripen\":true"));

    given().when().get("/fruitreact/name/lemon").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"lemon\""), containsString("\"ripen\":false"));

    given().when().get("/fruitreact/name/pineapple").then().statusCode(OK.getStatusCode())
      .body(containsString("\"name\":\"pineapple\""), containsString("\"ripen\":false"));
  }
}