package com.trikorasolutions.example.repo;

import com.trikorasolutions.example.dto.TreeDto;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.hibernate.Hibernate;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class TreeRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeRepository.class);
  @Inject
  Mutiny.SessionFactory sf;

  @ReactiveTransactional
  public Uni<Tree> create(Tree tree) {
    LOGGER.info("#createInRepo{}: ",tree);
    return sf.withTransaction((s, t) -> s.merge(tree))
      .replaceWith(sf.withTransaction((s, t) -> s.find(Tree.class, tree.name)));
  }

  @ReactiveTransactional
  public Uni<TreeDto> findByName(String name) {
    return sf.withTransaction((s, t) -> s.find(Tree.class, name)).onItem().transform(TreeDto::from);
  }

  @ReactiveTransactional
  public Uni<List<Tree>> listAll() {
    return sf.withSession(s -> s.createNamedQuery("Tree.listAll", Tree.class).getResultList());
  }

  @ReactiveTransactional
  public Uni<List<Tree>> findByFamily(String family) {
    return sf.withSession(s -> s.createNamedQuery("Tree.fetchFamily", Tree.class).setParameter("family", family).getResultList());
  }

  @ReactiveTransactional
  public Uni<Integer> delete(final String name) {
    return sf.withTransaction((s, t) -> s.createNamedQuery("Tree.delete").setParameter("name", name).executeUpdate());
  }
}
