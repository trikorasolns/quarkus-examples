package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.repo.FruitRepository;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FruitLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(FruitLogic.class);

  @Inject
  Mutiny.SessionFactory sf;

  @Inject
  FruitRepository repoFruit;

  /**
   * Changes the ripen state of a concrete family
   *
   * @param family Family that we want to set ripen == True
   * @return The number of registers that have been modified in the DB.
   */
  public Uni<Integer> ripe(final String family) {
    return sf.withTransaction((s, t) -> repoFruit.findByFamily(family).onItem()
        .invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen).forEach(fruit -> fruit.setRipen(Boolean.TRUE))))
          .onItem().transform(filteredList -> filteredList.size());
  }
}
