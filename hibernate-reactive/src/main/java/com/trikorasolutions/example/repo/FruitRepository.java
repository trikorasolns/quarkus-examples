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
    return sf.withSession(s -> s.createNamedQuery("Fruit.listAll", Fruit.class).getResultList());
  }

  public Uni<List<Fruit>> findByFamily(String family) {
    return sf.withSession(s -> s.createNamedQuery("Fruit.fetchFamily", Fruit.class).setParameter("family", family).getResultList());
  }

  public Uni<Integer> delete(final String name) {
    return sf.withTransaction((s, t) -> s.createNamedQuery("Fruit.delete").setParameter("name", name).executeUpdate());
  }

}
