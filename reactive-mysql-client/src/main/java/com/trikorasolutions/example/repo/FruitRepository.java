package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.model.Fruit;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

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
   * @param name Name od the fruit whose register is going to be deleted
   * @return The number of tuples that have been deleted from the DB.
   */
  public Uni<Integer> delete(final String name) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#delete(String) - {}", name);
    }

    return sqlPool.preparedQuery(
        " DELETE FROM fruit WHERE name = ?" )
      .execute(Tuple.of(name)).onItem()
      .transform(rowSet -> rowSet.rowCount() );
  }

  /**
   *
   * @return
   */
  public Uni<List<Fruit>> listAll() {
    return sqlPool.preparedQuery("SELECT * FROM fruit").execute().onItem()
      .transformToMulti(rows ->Multi.createFrom().iterable(rows))
      .onItem().transform(Fruit::from).collect().asList();
  }
















/*


  public Uni<List<Fruit>> findByFamily(String family) {
    return sf.withTransaction((s, t) -> s.createNamedQuery("Fruit.fetchFamily", Fruit.class).setParameter("family", family).getResultList());
  }

  public Uni<Integer> update(final String name) {
    return sf.withTransaction((s, t) -> s.createNamedQuery("Fruit.delete").setParameter("name", name).executeUpdate());
  }


*/
}
