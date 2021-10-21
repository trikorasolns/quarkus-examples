package com.trikorasolutions.example;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class KindOfPersistedEntitiesTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(KindOfPersistedEntitiesTest.class);

  @Test
  public void () {
    given().when().body(new Fruit("pear", "Pear", "Rosaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body("name", containsString("pear"), "description", containsString("Pear"));

    given().when().body(new Fruit("apple", "Apple", "Rosaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode())
      .body("name", containsString("apple"), "description", containsString("Apple"));

    given().when().body(new Fruit("lemon", "Lemon", "Rutaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode());

    given().when().body(new Fruit("pineapple", "Pineapple", "Rutaceae", false)).contentType(MediaType.APPLICATION_JSON)
      .post("/fruitreact/create").then().statusCode(OK.getStatusCode());

      given().when().contentType(MediaType.APPLICATION_JSON).get("/tree/combine2/Rosaceae/Rutaceae").then()
        .statusCode(OK.getStatusCode()).body("name",is("combine_tree"), "fruits.size()",is(4),
        "fruits.name", hasItems("apple","pear","lemon","pineapple"));
  }
}
