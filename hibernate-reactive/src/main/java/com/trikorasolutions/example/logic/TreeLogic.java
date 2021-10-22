package com.trikorasolutions.example.logic;

import com.trikorasolutions.example.dto.FruitDto;
import com.trikorasolutions.example.dto.TreeDto;
import com.trikorasolutions.example.model.Fruit;
import com.trikorasolutions.example.model.Tree;
import com.trikorasolutions.example.repo.FruitRepository;
import com.trikorasolutions.example.repo.TreeRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TreeLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(TreeLogic.class);
  @Inject
  FruitRepository repoFruit;

  @Inject
  TreeRepository repoTree;

  @Inject
  Mutiny.SessionFactory sf;

  @ReactiveTransactional
  public Uni<TreeDto> create(final TreeDto tree) {
    LOGGER.info("#createInLogic(String)... {}", tree.name);
    return repoTree.create(tree.toTree())
      .onItem().transform(TreeDto::from)
      .replaceWith(this.getFullTree(tree.name));

  }

  public Uni<TreeDto> getFullTree(String treeName) {
    LOGGER.info("#getFullTree(String){}: ", treeName);
    TreeDto newTree = new TreeDto(treeName);

    return sf.withTransaction(
      (s, t) -> s.find(Tree.class, treeName)
        .chain(fTree -> repoFruit.findByTree(fTree.name))
        .onItem().transform(FruitDto::allFrom)
        .invoke(dbTree -> newTree.setFruits(dbTree))
        .replaceWith(newTree));
  }

  @ReactiveTransactional
  public Uni<TreeDto> findToCombine(String family1, String family2) {
    LOGGER.info("#findToCombine(logic) f1:{}, f2{}", family1, family2);

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

    }).onItem().transformToUni(tree->getFullTree(tree.name));
  }

  @ReactiveTransactional
  public Uni<TreeDto> persistAlreadyCreated(Tree tree, String family) {
    LOGGER.info("#persistAlreadyCreated(logic) {}", tree.name);
    Uni<List<Fruit>> listF1 = repoFruit.findByFamily(family);

    return listF1.onItem().transformToUni(list -> {
      tree.setTreeFruits(list);
      return repoTree.create(tree); // Returns find tree

    }).onItem().transformToUni(treeF->getFullTree(treeF.name));

  }

  @ReactiveTransactional
  public Uni<TreeDto> persistNoneCreated(TreeDto tree) {
    LOGGER.info("#persistNoneCreated(logic) {}", tree.name);

    // Insert the new tree and the new fruits just by persisting the tree
    return repoTree.create(tree.toTree())
      .onItem().transformToUni(treeF->getFullTree(treeF.name));
  }

  @ReactiveTransactional
  public Uni<TreeDto> persistOnlyFruits(List<FruitDto> fruits, final String treeName) {
    LOGGER.info("#persistNoneCreated(logic) {}", treeName);

    // Get the tree from the db
    Uni<Tree> tree = sf.withTransaction((s, t) -> s.find(Tree.class, treeName));
    // Parse Dto to fruits
    List<Fruit> lst = fruits.stream().map(f-> f.toFruit()).collect(Collectors.toList());

    return tree.onItem().invoke(t-> t.addFruits(lst))
      .onItem().call(tr->repoTree.create(tr))
      .onItem().transformToUni(treeF->getFullTree(treeF.name));
  }



}


