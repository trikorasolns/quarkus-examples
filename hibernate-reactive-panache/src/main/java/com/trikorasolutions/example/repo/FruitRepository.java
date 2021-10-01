package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.model.Fruit;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FruitRepository implements PanacheRepositoryBase<Fruit, String> {

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
   * @param fruit Fruit that is going to be persisted in the DB.
   * @return A new updated instance of the Fruit that has been persisted in the DB.
   */
  @ReactiveTransactional
  public Uni<Fruit> change(Fruit fruit) {
    return this.findById(fruit.name).onItem().call(f->{
      f.setDescription(fruit.description);
      f.setFamily(fruit.family);
      f.setRipen(fruit.ripen);
      return f.persist();}
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
