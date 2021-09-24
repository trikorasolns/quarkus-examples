package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.repo.FruitRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FruitLogic {
  @Inject
  FruitRepository repoFruit;

  /**
   * TODO This method no not persist
   * Changes the ripen state of a concrete family
   *
   * @param family Family that we want to set ripen == True
   * @return The number of registers that have been modified in the DB.
   */
  public Uni<Integer> ripe(final String family) {
    return repoFruit.findByFamily(family).onItem()
      .invoke(fruits -> fruits.stream().filter(fruit -> !fruit.ripen).forEach(fruit -> repoFruit.change(fruit).onItem().transform(fruit1 -> fruit1)))
          .onItem().transform(filteredList -> filteredList.size());
  }

}