package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.logic.FruitLogic;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.repo.FruitRepository;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
@Path("/fruitreact")
@Produces("application/json")
@Consumes("application/json")
public class FruitReactiveResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(FruitReactiveResource.class);

  @Inject
  FruitRepository repoFruit;

  @Inject
  FruitLogic logicFruit;

  @POST
  @Path("/create")
  public Uni<Response> create(final Fruit fruit) {
    return repoFruit.create(fruit).onItem().transform(fruit1 -> {
      LOGGER.info("created fruit: {}", fruit1);
      return Response.ok(fruit1).build();
    }).onFailure().recoverWithItem(thr -> {
      LOGGER.info("Created duplicated fruit");
      return Response.ok().status(CONFLICT).build();
    });
  }

  @DELETE
  @Path("/{name}")
  public Uni<Response> delete(final @RestPath String name) {
    return repoFruit.delete(name).onItem().transform(delCount -> {
      LOGGER.info("deleted fruit: {}", delCount);
      if (delCount > 0) {
        return Response.ok(delCount).build();
      } else {
        return Response.ok().status(NOT_FOUND).build();
      }
    });
  }

  @GET
  @Path("/name/{name}")
  public Uni<Response> findByName(final @RestPath String name) {
    return repoFruit.findByName(name).onItem().transform(fruit -> {
      LOGGER.info("fetched fruit: {}", fruit);
      if (fruit != null) {
        return Response.ok(fruit).build();
      } else {
        return Response.ok().status(NOT_FOUND).build();
      }
    });
  }

  @GET
  @Path("/listAll")
  public Uni<Response> listAll(final @PathParam("name") String name) {
    return repoFruit.listAll().onItem().transform(fruit -> {
      return Response.ok(fruit).build();
    });
  }

  @PUT
  @Path("/ripe/{family}")
  public Uni<Response> ripeFamily(final @RestPath String family) {
    LOGGER.info("Familia {Uni<List<Fruit>>}",logicFruit.ripe(family));
    return logicFruit.ripe(family).onItem().transform(delCount -> {
      LOGGER.info("[FUERA]ripen fruit: {}", delCount);
      if (delCount > 0) {
        return Response.ok(delCount).build();
      } else {
        return Response.ok().status(NOT_FOUND).build();
      }
    });
  }

}