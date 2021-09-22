package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.repo.FruitRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClientHelper;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FruitLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(FruitLogic.class);

  @Inject
  io.vertx.mutiny.mysqlclient.MySQLPool sqlPool;

  @Inject
  FruitRepository repoFruit;

  public Uni<Integer> ripe(final String family) {
    LOGGER.info("ENTERING IN LOGIC FRUIT");

    return SqlClientHelper.inTransactionUni(sqlPool, tx ->
      repoFruit.findByFamily(family).onItem()
        .invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen).forEach(f -> repoFruit.update(f.name, Boolean.TRUE)))
//        .invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen).forEach(f -> sqlPool.preparedQuery("UPDATE fruit SET ripen = ? WHERE name = ?").execute(
//            Tuple.of(Boolean.TRUE, f.name))
//          .onItem().transform(rowSet -> Fruit.from(rowSet.iterator().next()))))

    ).onItem().transform(filteredList -> filteredList.size());

  }
}
