package com.trikorasolutions.example.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.trikorasolutions.example.dto.TreeDto;
import com.trikorasolutions.example.logic.TreeLogic;
import com.trikorasolutions.example.repo.TreeRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.Arrays;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
@Path("/tree")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TreeReactiveResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeReactiveResource.class);

  @Inject
  TreeRepository repoTree;

  @Inject
  TreeLogic logicTree;

  @POST
  @Path("/create")
  public Uni<RestResponse<TreeDto>> create(final TreeDto tree) {
    LOGGER.info("#create(TreeDto)... {}", tree);
    return logicTree.create(tree).onItem().transform(tree1 -> RestResponse.ResponseBuilder.ok(tree1).build())
      .onFailure().recoverWithItem(ex -> {
        LOGGER.error("ex: {}", ex);
        return RestResponse.ResponseBuilder.create(Response.Status.CONFLICT, new TreeDto()).build();
      });
  }

  @GET
  @Path("/name/{name}")
  public Uni<RestResponse<TreeDto>> getTree(final @RestPath("name") String name) {
    return repoTree.findByName(name).onItem().transform(tree -> {
      LOGGER.info("fetched tree: {}", tree);
      if (tree != null) {
        return RestResponse.ResponseBuilder.ok(tree).build();
      } else {
        return RestResponse.ResponseBuilder.create(NOT_FOUND, new TreeDto()).build();
      }
    });
  }

  @GET
  @Path("getFull/name/{name}")
  public Uni<RestResponse<TreeDto>> getFullTree(final @RestPath("name") String name) {
    LOGGER.info("#getFullTree(String): {}", name);
    return logicTree.getFullTree(name).onItem().transform(tree -> {
      LOGGER.info("fetched tree: {}", tree);
      if (tree != null) {
        return RestResponse.ResponseBuilder.ok(tree).build();
      } else {
        return RestResponse.ResponseBuilder.create(NOT_FOUND, new TreeDto()).build();
      }
    });
  }

  @DELETE
  @Path("/{name}")
  @ReactiveTransactional
  public Uni<Response> delete(final @RestPath String name) {
    return repoTree.delete(name).onItem().transform(deleted -> {
      LOGGER.info("deleted tree: {}", deleted);
      if (deleted > 0) {
        return Response.ok(deleted).build();
      } else {
        return Response.ok().status(NOT_FOUND).build();
      }
    });
  }

  @GET
  @Path("/listAll")
  public Uni<Response> listAll() {
    return repoTree.listAll().onItem().transform(tree -> Response.ok(tree).build());
  }

  @GET
  @Path("/combine1/{family1}")
  public Uni<RestResponse<TreeDto>> combine1(final @RestPath String family1) {
    LOGGER.info("#combine1 f1 {}", family1 );

    return logicTree.addFruitsToTree(family1).onItem().transform(fruits -> {
      LOGGER.info("combined fruit: {}", fruits);
      return RestResponse.ResponseBuilder.ok(fruits).build();

    }).onFailure().recoverWithItem(thr -> {
      LOGGER.info("Failure when calling findToCombine:{}", thr);
      return RestResponse.ResponseBuilder.create(Response.Status.NOT_ACCEPTABLE, new TreeDto()).build();
    });
  }

  @POST
  @Path("/combine2")
  public Uni<RestResponse<TreeDto>> combine2(final @RestPath String family1, final @RestPath String family2) {
    //TODO: fix the rest path arguments
    LOGGER.info("#combined f1,f2 {}-{}", "Rutaceae", "Rosaceae");

    return logicTree.findToCombine(family1, family2).onItem().transform(fruits -> {
      LOGGER.info("combined fruit: {}", fruits);
      return RestResponse.ResponseBuilder.ok(fruits).build();

    }).onFailure().recoverWithItem(thr -> {
      LOGGER.info("Failure when calling findToCombine:{}", thr);
      return RestResponse.ResponseBuilder.create(Response.Status.NOT_ACCEPTABLE, new TreeDto()).build();
    });
  }

}