package com.trikorasolutions.example.resource;


import com.trikorasolutions.example.dto.TreeDto;
import com.trikorasolutions.example.logic.TreeLogic;
import com.trikorasolutions.example.model.Tree;
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

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.CONFLICT;

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
  public Uni<Response> create(final Tree tree) {
    return repoTree.create(tree).onItem().transform(tree1 -> {
      LOGGER.info("created tree: {}", tree1);
      return Response.ok(tree1).build();
    }).onFailure().recoverWithItem(ex -> {
      LOGGER.info("ex: {}", ex);
      return Response.ok().status(CONFLICT).build();
    });
  }

  @GET
  @Path("/name/{name}")
  public Uni<Response> findByName(final @RestPath("name") String name) {
    return repoTree.findByName(name).onItem().transform(tree -> {
      LOGGER.info("fetched tree: {}", tree);
      if (tree != null) {
          return Response.ok(TreeDto.from(tree)).build();
      } else {
          return Response.ok().status(NOT_FOUND).build();
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


  @POST
  @Path("/combine")
  public Uni<RestResponse<TreeDto>> combine(final @RestPath  String family1, final @RestPath String family2) {
    //TODO: fix the rest path arguments
    LOGGER.info("#combined f1,f2 {}-{}", "Rutaceae", "Rosaceae");

    return logicTree.findToCombine("Rutaceae","Rosaceae").onItem().transform(fruits -> {
      LOGGER.info("combined fruit: {}", fruits);
      return RestResponse.ResponseBuilder.ok(fruits).build();

    }).onFailure().recoverWithItem(thr -> {
      LOGGER.info("Failure when calling findToCombine:{}",thr);
      return RestResponse.ResponseBuilder.create(Response.Status.NOT_ACCEPTABLE, new TreeDto()).build();
    });
  }

}