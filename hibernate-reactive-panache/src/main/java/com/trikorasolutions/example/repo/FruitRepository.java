package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FruitRepository implements PanacheRepositoryBase<Fruit, String> {
  @Inject
  TreeRepository repoTree;

  private static final Logger LOGGER = LoggerFactory.getLogger(FruitRepository.class);

  /**
   * Persist a Fruit in the DB.
   *
   * @param fruit Fruit that is going to be persisted in the DB.
   * @return A new instance of the Fruit that has been persisted in the DB.
   */
  @ReactiveTransactional
  public Uni<Fruit> create(Fruit fruit) {
    return this.persist(fruit);
  }

  /**
   * Update the fields of a fruit but preserving its id (name)
   *
   * @param fruit Fruit with the updated fields that are going to be persisted in the DB.
   * @return A new updated instance of the Fruit that has been persisted in the DB.
   */
  @ReactiveTransactional
  public Uni<Fruit> change(Fruit fruit) {
    return this.findById(fruit.name).onItem().call(f->{
      f.setDescription(fruit.description);
      f.setFamily(fruit.family);
      f.setRipen(fruit.ripen);
      return this.persist(f);}
    );
  }

  /**
   * Search a fruit in the DB.
   *
   * @param name name of the fruit that is going to be searched in the DB.
   * @return A new instance of a Fruit object if it is already stored in the DB or
   * null otherwise.
   */
  public Uni<Fruit> findByName(String name) {
    return this.findById(name);
  }

  /**
   * Delete a fruit from the DB.
   *
   * @param name Name od the fruit whose register is going to be deleted.
   * @return The number of tuples that have been deleted from the DB.
   */
  @ReactiveTransactional
  public Uni<Boolean> remove(final String name) {
    return this.deleteById(name);
  }

  /**
   * Search all the fruits of a concrete family in the DB.
   *
   * @param family The kind of fruit that we want to search in the DB.
   * @return A list with the desired fruits.
   */
  public Uni<List<Fruit>> findByFamily(String family) {
    return this.list("family", family);
  }

}
