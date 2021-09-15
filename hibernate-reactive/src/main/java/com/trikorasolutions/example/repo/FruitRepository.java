package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.model.Fruit;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FruitRepository {
  @Inject
  Mutiny.SessionFactory sf;

  public Uni<Fruit> create(Fruit fruit) {
    return sf.withTransaction((s, t) -> s.persist(fruit))
      .replaceWith(sf.withTransaction((s, t) -> s.find(Fruit.class, fruit.name)));
  }

  public Uni<Fruit> findByName(String name) {
    return sf.withTransaction((s, t) -> s.find(Fruit.class, name));
  }

  public Uni<List<Fruit>> listAll() {
    return sf.withTransaction((s, t) -> s.createNamedQuery("Fruit.listAll", Fruit.class).getResultList());
  }

  public Uni<Integer> delete(final String name) {
    return sf.withTransaction((s, t) -> s.createNamedQuery("Fruit.delete").setParameter("name", name).executeUpdate());
  }

//  public Uni<Integer> ripe(final String family) {
//    return sf.withTransaction(
//      (s, t) -> s.createNamedQuery("Fruit.fetchFamily", Fruit.class).setParameter("family", family).getResultList()
//        .onItem().transform(lstFruits -> {
////          Integer updateCount = 0;
//          lstFruits.forEach(fruit -> {
//            if (!fruit.isRipen) {
//              fruit.setRipen(true);
////              updateCount++;
//            }
//          });
////          return updateCount;
//        }));
//  }

}
