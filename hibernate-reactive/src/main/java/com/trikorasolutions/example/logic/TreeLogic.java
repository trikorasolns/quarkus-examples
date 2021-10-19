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

    // Gets the families in two gets using the NamedQueries
    Uni<List<Fruit>> listF1 = repoFruit.findByFamily(family1);
    Uni<List<Fruit>> listF2 = repoFruit.findByFamily(family2);

    // Combine them in a table
    Uni<Tuple2<List<Fruit>, List<Fruit>>> fruitTuple = Uni.combine().all().unis(listF1, listF2).asTuple();

    // Put all the fruits in a new the tree
    return fruitTuple.onItem().transformToUni(tuple -> {
        Tree tree = new Tree("combine_tree");
        List<Fruit> lst = new ArrayList<>();
        lst.addAll(tuple.getItem1());
        lst.addAll(tuple.getItem2());

        tree.setTreeFruits(lst);
        return repoTree.create(tree); // Returns find tree

      })/*.onItem().transformToUni( x-> Mutiny.fetch(x.getTreeFruits())
        .onItem().transform(fruits -> {
        x.setTreeFruits(fruits);
        return x;

      }))*/.onItem().transform(TreeDto::from);

  }
}

