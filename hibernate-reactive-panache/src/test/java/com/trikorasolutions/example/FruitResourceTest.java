package com.trikorasolutions.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@QuarkusTest
public class FruitResourceTest {

    @Test
    public void testGetFruitUnknown() {
        given()
                .when().get("/fruit/name/unknown")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetFruitPear() {
        given()
                .when().get("/fruit/name/pear")
                .then()
                .statusCode(200)
                .body(is("Fruit<1>"));
    }

}