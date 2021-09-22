package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.repo.FruitRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClientHelper;
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
    return SqlClientHelper.inTransactionUni(sqlPool, tx -> {
      repoFruit.findByFamily(family).onItem()
        .invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen).forEach(f -> repoFruit.update(f.name, f.setRipen(Boolean.TRUE))));
      return repoFruit.findByFamily(family).onItem().invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen));
    }).onItem().transform(fruits -> fruits.size());
//    sqlPool.withTransaction((client, tx)-> repoFruit.findByFamily(family).onItem()
//      .invoke(fruits -> fruits.stream().forEach(f->f.setRipen(Boolean.TRUE))))
//      .onItem().transform(fruits -> fruits.size());
  }

//public Uni<Integer> ripe(final String family) {
//  return sf.withTransaction((s, t) -> repoFruit.findByFamily(family).onItem()
//      .invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen).forEach(fruit -> fruit.setRipen(Boolean.TRUE))))
//    .onItem().transform(fruits -> fruits.size());
}
