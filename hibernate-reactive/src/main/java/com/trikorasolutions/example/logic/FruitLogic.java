package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.repo.FruitRepository;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FruitLogic {
  @Inject
  Mutiny.SessionFactory sf;

  @Inject
  FruitRepository repoFruit;

  public Uni<Integer> ripe(final String family) {
    sf.withTransaction((s, t) -> repoFruit.findByFamily(family).onItem().invoke(lstFruits -> {
      lstFruits.stream().filter(fruit -> fruit.ripen).forEach(fruit -> {
        fruit.setRipen(true);
        s.persist(fruit);
      });
    }));
    return Uni.createFrom().item(0);
  }

}
