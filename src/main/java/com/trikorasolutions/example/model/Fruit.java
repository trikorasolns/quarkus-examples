package com.trikorasolutions.example.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;

@Entity
@ApplicationScoped
public class Fruit extends PanacheEntity {

    public String name;
    public String description;

    public Fruit() {
    }

    public Fruit(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Uni<Fruit> findByName(String name) {
        return find("name", name).firstResult();
    }

    // return name as uppercase in the model
    public String getName() {
        return name.toUpperCase();
    }

    // store all names in lowercase in the DB
    public void setName(String name) {
        this.name = name.toLowerCase();
    }

}