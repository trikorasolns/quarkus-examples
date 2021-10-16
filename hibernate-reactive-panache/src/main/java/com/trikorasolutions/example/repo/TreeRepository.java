package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.model.Tree;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TreeRepository implements PanacheRepositoryBase<Tree, String> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeRepository.class);

  /**
   * Persist a Tree in the DB.
   *
   * @param Tree Tree that is going to be persisted in the DB.
   * @return A new instance of the Tree that has been persisted in the DB.
   */
  @ReactiveTransactional
  public Uni<Tree> create(final Tree Tree) {
    return this.persist(Tree);
  }

  /**
   * Delete a Tree from the DB.
   *
   * @param name Name of the Tree whose register is going to be deleted.
   * @return The number of tuples that have been deleted from the DB.
   */
  @ReactiveTransactional
  public Uni<Boolean> remove(final String name) {
    return this.deleteById(name);
  }

  /**
   * Update the fields of a fruit but preserving its id (name)
   *
   * @param tree Tree that is going to be persisted in the DB.
   * @return A new updated instance of the Fruit that has been persisted in the DB.
   */
  @ReactiveTransactional
  public Uni<Tree> change(Tree tree) {
    LOGGER.info("REPO UPDATE{}", tree);

    return this.findById(tree.name).onItem().call(f -> {
      tree.setTreeFruits(tree.getTreeFruits());
      return f.persist();
    });
  }
}
