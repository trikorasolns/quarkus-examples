package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.repo.FruitRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FruitLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(FruitLogic.class);
  @Inject
  FruitRepository repoFruit;

  /**
   * Changes the ripen state of a concrete family
   *
   * @param family Family that we want to set ripen == True
   * @return The number of registers that have been modified in the DB.
   */
  @ReactiveTransactional
  public Uni<Integer> ripe(final String family) {

    return repoFruit.findByFamily(family)
      .onItem().invoke(fruits -> fruits.stream()
        .filter(fruit -> !fruit.ripen)
        .forEach(fruit ->{fruit.setRipen(true); repoFruit.persist(fruit);}))
      .onItem().transform(findByFam -> findByFam.size());
  }
}