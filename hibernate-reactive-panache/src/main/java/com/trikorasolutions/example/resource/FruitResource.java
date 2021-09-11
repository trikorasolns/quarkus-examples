package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.model.Fruit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
@Path("/fruit")
public class FruitResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/name/{name}")
    public Response getFruit(final @PathParam("name") String fruitName) {
        return Fruit.findByName(fruitName).onItem().transform(fruit -> {
            if (fruit != null) {
                return Response.ok(fruit).build();
            } else {
                return Response.ok().status(NOT_FOUND).build();
            }
        }).await().atMost(Duration.ofSeconds(30));
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("/name/{name}")
//    public Response create(final Fruit fruit) {
//        return fruit.findByName(fruitName).onItem().transform(fruitObj -> {
//            if (fruitObj != null) {
//                return Response.ok(fruitObj).build();
//            } else {
//                return Response.ok().status(NOT_FOUND).build();
//            }
//        }).await().atMost(Duration.ofSeconds(30));
//    }
}