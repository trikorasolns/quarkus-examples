package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.Startup;
import com.trikorasolutions.example.model.Fruit;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
@Path("/fruitreact")
public class FruitReactiveResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/name/{name}")
    public Uni<Response> findByName(final @PathParam("name") String name) {
        return Fruit.findByName(name).onItem().transform(fruit -> {
            LOGGER.warn("fruit: {}", fruit);
            if (fruit != null) {
                return Response.ok(fruit).build();
            } else {
                return Response.ok().status(NOT_FOUND).build();
            }
        });
    }

}