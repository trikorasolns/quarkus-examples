package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.dto.TreeDto;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import com.trikorasolutions.example.repo.FruitRepository;
import com.trikorasolutions.example.repo.TreeRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TreeLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeLogic.class);
  @Inject
  FruitRepository repoFruit;

  @Inject
  TreeRepository repoTree;

  @ReactiveTransactional
  public Uni<TreeDto> findToCombine(String family1, String family2) {
    LOGGER.info("#findToCombine f1:{}, f2{}", family1, family2);

    // Gets the families in two gets
    Uni<List<Fruit>> listF1 = repoFruit.find("family = :family order by name", Parameters.with("family", family1))
      .list();
    Uni<List<Fruit>> listF2 = repoFruit.find("family = :family order by name", Parameters.with("family", family2))
      .list();

    // Combine them in a table
    Uni<Tuple2<List<Fruit>, List<Fruit>>> fruitTuple = Uni.combine().all().unis(listF1, listF2).asTuple();

    // Put all the fruits in a new the tree
    return fruitTuple.onItem().transformToUni(tuple -> {
        Tree tree = new Tree("combine_tree");
        List<Fruit> lst = new ArrayList<>();
        lst.addAll(tuple.getItem1());
        lst.addAll(tuple.getItem2());
        lst.forEach(fruit -> fruit.setTree(tree.name));

        tree.setTreeFruits(lst);
        return tree.persist();

      }).onItem().transformToUni(tree -> repoTree.findById(((Tree) tree).name)).onItem()
      .transformToUni(tree -> repoFruit.findForTree(tree.name); Mutiny.fetch(tree.getTreeFruits()).onItem().transform(fruits -> {
        tree.setTreeFruits(fruits);
        return tree;
      })).onItem().transform(TreeDto::from);


  }
}
