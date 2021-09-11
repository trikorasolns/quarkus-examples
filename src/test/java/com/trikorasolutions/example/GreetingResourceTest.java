package com.trikorasolutions.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello RESTEasy"));
    }

    @Test
    public void testGetFruitUnknown() {
        given()
                .when().get("/hello/fruit/name/unknown")
                .then()
                .statusCode(500);
    }

    @Test
    public void testGetFruitPear() {
        given()
                .when().get("/hello/fruit/name/pear")
                .then()
                .statusCode(200)
                .body(is("Fruit<1>"));
    }

}