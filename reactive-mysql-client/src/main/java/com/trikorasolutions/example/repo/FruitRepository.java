package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.model.Fruit;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FruitRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(FruitRepository.class);

  @Inject
  io.vertx.mutiny.mysqlclient.MySQLPool sqlPool;

  /**
   * Persist a Fruit in the DB.
   *
   * @param fruit Fruit that is going to be persisted in the DB.
   * @return A new instance of the Fruit that has been persisted in the DB.
   */
  public Uni<Fruit> create(Fruit fruit) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#create(Fruit) - {}", fruit);
    }
    return sqlPool.preparedQuery(
        " INSERT INTO fruit (name, description, family, ripen) VALUES (?, ?, ?, ?) RETURNING *")
      .execute(Tuple.of(fruit.name, fruit.description, fruit.family, fruit.ripen)).onItem()
      .transform(rowSet -> Fruit.from(rowSet.iterator().next()));
  }

  /**
   * Search a fruit in the DB.
   *
   * @param name name of the fruit that is going to be searched in the DB.
   * @return A new instance of a Fruit object if it is already stored in the DB or
   * null otherwise.
   */
  public Uni<Fruit> findByName(String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#findByName(String) - {}", name);
    }
    return sqlPool.preparedQuery("SELECT * FROM fruit WHERE name = ?").execute(Tuple.of(name)).onItem()
      .transform(RowSet::iterator).onItem()
      .transform(iterator -> iterator.hasNext() ? Fruit.from(iterator.next()) : null);
  }

  /**
   * Delete a fruit from the DB.
   *
   * @param name Name od the fruit whose register is going to be deleted.
   * @return The number of tuples that have been deleted from the DB.
   */
  public Uni<Fruit> update(final String name, final Fruit fruit) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#delete(String) - {}", name);
    }

    return sqlPool.preparedQuery(" DELETE FROM fruit WHERE name = ?").execute(Tuple.of(name)).onItem()
      .transform(rowSet -> rowSet.rowCount());
  }

  /**
   * Delete a fruit from the DB.
   *
   * @param name Name od the fruit whose register is going to be deleted.
   * @return The number of tuples that have been deleted from the DB.
   */
  public Uni<Integer> delete(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#delete(String) - {}", name);
    }

    return sqlPool.preparedQuery(" DELETE FROM fruit WHERE name = ?").execute(Tuple.of(name)).onItem()
      .transform(rowSet -> rowSet.rowCount());
  }

  /**
   * List all the elements that are available in the fruit table.
   *
   * @return A list with all the tuples of the table.
   */
  public Uni<List<Fruit>> listAll() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#listAll(String) ");
    }

    return sqlPool.preparedQuery("SELECT * FROM fruit").execute().onItem()
      .transformToMulti(rows -> Multi.createFrom().iterable(rows)).onItem().transform(Fruit::from).collect().asList();
  }

  /**
   * Search all the fruits of a concrete family in the DB.
   *
   * @param family The kind of fruit that we want to search in the DB.
   * @return A list with the desired fruits.
   */
  public Uni<List<Fruit>> findByFamily(String family) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#findByFamily(String) - {}", family);
    }
    return sqlPool.preparedQuery("SELECT * FROM fruit where family =?").execute(Tuple.of(family)).onItem()
      .transformToMulti(rows -> Multi.createFrom().iterable(rows)).onItem().transform(Fruit::from).collect().asList();
//    return sqlPool.preparedQuery("SELECT f FROM Fruit f where family =?").execute(Tuple.of(family)).onItem()
//      .transformToMulti(rows -> Multi.createFrom().iterable(rows)).onItem().transform(Fruit::from).collect().asList();
//    return sqlPool.preparedQuery("SELECT * FROM fruit where family =?").execute(Tuple.of(family)).onItem()
//      .transformToMulti(rows -> Multi.createFrom().iterable(rows)).onItem().transform(Fruit::from).collect().asList();
  }

}
