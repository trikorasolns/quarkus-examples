package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.model.Fruit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;

@Path("/hello")
public class GreetingResource {

    @Inject
    Fruit fruit;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/fruit/name/{name}")
    public Response getFruit(final @PathParam("name") String fruitName) {
        return fruit.findByName(fruitName).onItem().transform(fruitObj -> {
            if (fruitObj != null) {
                return Response.ok(fruitObj).build();
            } else {
                return Response.serverError().build();
            }
        }).await().atMost(Duration.ofSeconds(30));
    }
}