package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
public class FruitReactiveResourceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FruitReactiveResourceTest.class);

  @Inject
  Mutiny.SessionFactory sf;

  @BeforeEach
  public void initDatabase() {
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
  public void testGet() {
    given().when().get("/fruitreact/name/unknown").then().statusCode(NOT_FOUND.getStatusCode());

    given().when().get("/fruitreact/name/pear").then().statusCode(OK.getStatusCode())
      .body("name", containsString("pear"), "description", containsString("Pear"));
  }

  @Test
  public void testCreate() {
    given().when().contentType(MediaType.APPLICATION_JSON).body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType("application/json")
      .post("/fruitreact/create").then().statusCode(CONFLICT.getStatusCode());
  }

  @Test
  public void testListAll() {
    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode())
      .body("$.size()", is(2), "name", containsInAnyOrder("pear","apple")
        , "description", containsInAnyOrder("Pear","Apple")
        , "family", containsInAnyOrder("Rosaceae","Rosaceae"));

    given().when().delete("/fruitreact/pear").then().statusCode(OK.getStatusCode());
    given().when().delete("/fruitreact/apple").then().statusCode(OK.getStatusCode());

    given().when().get("/fruitreact/listAll").then().statusCode(OK.getStatusCode())
      .body("$.size()", is(0));
  }

  @Test
  public void testDelete() {
    given().when().delete("/fruitreact/lemon").then().statusCode(NOT_FOUND.getStatusCode());

    given().when().delete("/fruitreact/pear").then().statusCode(OK.getStatusCode());

    given().when().get("/fruitreact/name/pear").then().statusCode(NOT_FOUND.getStatusCode());
  }

  @Test
  public void testUpdate() {
    given().when()
      .body(new Fruit("pear", "Updated-Pear", "PearFam", true))
      .contentType(MediaType.APPLICATION_JSON)
      .put("/fruitreact/update/pear").then().statusCode(OK.getStatusCode())
    ;

    given().when().get("/fruitreact/name/pear").then().statusCode(OK.getStatusCode())
      .body("name", containsString("pear"), "description", containsString("Updated-Pear"),
      "family", containsString("PearFam"), "ripen", is(true))
    ;

    given().when()
      .body(new Fruit("Unknown", "Unknown", "Unknown", true))
      .contentType(MediaType.APPLICATION_JSON)
      .put("/fruitreact/update/Unknown").then().statusCode(CONFLICT.getStatusCode())
    ;
  }

  @Test
  public void testLogic() {
    given().when().body(new Fruit("lemon", "Lemon", "Rutaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode());

    given().when().body(new Fruit("pineapple", "Pineapple", "Rutaceae_2", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode());

    given().when().put("/fruitreact/ripe/Rosaceae").then().statusCode(OK.getStatusCode());

    given().when().get("/fruitreact/name/pear").then().statusCode(OK.getStatusCode())
      .body("name", containsString("pear"), "description", containsString("Pear"),
        "family", containsString("Rosaceae"), "ripen", is(true))
    ;

    given().when().get("/fruitreact/name/apple").then().statusCode(OK.getStatusCode())
      .body("name", containsString("apple"), "description", containsString("Apple"),
        "family", containsString("Rosaceae"), "ripen", is(true))
    ;

    given().when().get("/fruitreact/name/lemon").then().statusCode(OK.getStatusCode())
      .body("name", containsString("lemon"), "description", containsString("Lemon"),
        "family", containsString("Rutaceae"), "ripen", is(false))
    ;

    given().when().get("/fruitreact/name/pineapple").then().statusCode(OK.getStatusCode())
      .body("name", containsString("pineapple"), "description", containsString("Pineapple"),
        "family", containsString("Rutaceae_2"), "ripen", is(false))
    ;
  }
}