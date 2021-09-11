package com.trikorasolutions.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class FruitReactiveResourceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

    @Test
    public void testGetFruitReactiveUnknown() {
        given()
                .when().get("/fruitreact/name/unknown")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetFruitReactivePear() {
        Response response = given()
                .when().get("/fruitreact/name/pear")
                .then()
                .statusCode(200).extract().response();
        LOGGER.warn("response: {}", response);
        assertEquals("Fruit<1>", response.body().print());
//        assertThat("Is Fruit<1>", .prettyPrint().contains("Fruit<1>"));
    }

}