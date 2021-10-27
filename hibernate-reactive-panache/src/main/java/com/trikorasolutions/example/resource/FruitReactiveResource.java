package com.trikorasolutions.example.resource;

import com.trikorasolutions.example.Startup;
import com.trikorasolutions.example.dto.TreeDto;
import com.trikorasolutions.example.logic.FruitLogic;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import com.trikorasolutions.example.repo.FruitRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CONFLICT;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_ACCEPTABLE;

@ApplicationScoped
@Path("/fruitreact")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
      LOGGER.debug("created fruit: {}", fruit1);
      return Response.ok(fruit1).build();

    }).onFailure().recoverWithItem(thr -> {
      LOGGER.debug("Created duplicated fruit");
      return Response.ok().status(CONFLICT).build();
    });
  }

  @GET
  @Path("/name/{name}")
  public Uni<Response> findByName(final @RestPath("name") String name) {
    return repoFruit.findByName(name).onItem().transform(fruit -> {
      LOGGER.debug("fetched fruit: {}", fruit);
      if (fruit != null) {
          return Response.ok(fruit).build();
      } else {
          return Response.ok().status(NOT_FOUND).build();
      }
    });
  }

  @DELETE
  @Path("/{name}")
  @ReactiveTransactional
  public Uni<Response> delete(final @RestPath String name) {
    return repoFruit.remove(name).onItem().transform(deleted -> {
      LOGGER.debug("deleted fruit: {}", deleted);
      if (deleted) {
        return Response.ok().build();
      } else {
        return Response.ok().status(NOT_FOUND).build();
      }
    });
  }

  @GET
  @Path("/listAll")
  public Uni<Response> listAll() {
    return repoFruit.listAll().onItem().transform(fruit -> Response.ok(fruit).build());
  }

  @PUT
  @Path("/update")
  public Uni<Response> update(final Fruit fruit) {
    return repoFruit.change(fruit).onItem().transform(fruit1 -> {
      LOGGER.debug("updated fruit: {}", fruit1);
      return Response.ok(fruit1).build();

    }).onFailure().recoverWithItem(thr -> {
      LOGGER.debug("The Fruit that you are trying to update does not exit {}");
      return Response.ok(thr.getMessage()).status(CONFLICT).build();
    });
  }

  @PUT
  @Path("/ripe/{family}")
  public Uni<Response> ripeFamily(final @RestPath String family) {
    return logicFruit.ripe(family).onItem().transform(delCount -> {
      if (delCount > 0) {
        return Response.ok(delCount).build();
      } else {
        return Response.ok().status(NOT_FOUND).build();
      }
    });
  }
}