package com.trikorasolutions.example.model;

import io.vertx.mutiny.sqlclient.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "fruit")
@NamedQuery(name = "Fruit.listAll", query = "SELECT f FROM Fruit f ORDER BY f.name")
@NamedQuery(name = "Fruit.delete", query = "DELETE FROM Fruit f WHERE f.name = :name")
@NamedQuery(name = "Fruit.fetchFamily", query = "SELECT f FROM Fruit f WHERE f.family = :family")
public class Fruit {
  private static final Logger LOGGER = LoggerFactory.getLogger(Fruit.class);

  @Id
  @Column(length = 50, unique = true)
  public String name;

  @Column(length = 200)
  public String description;

  @Column(length = 50)
  public String family;

  @Column(nullable = false)
  public Boolean ripen = false;

  public Fruit() {
  }

  public Fruit(String name) {
    this.name = name;
    this.description = name;
  }

  public Fruit(String name, String description) {
    this(name);
    this.description = description;
  }

  public Fruit(String name, String description, String family, Boolean isRipen) {
    this(name, description);
    this.family = family;
    this.ripen = isRipen;
  }

  /**
   * Generates a Fruit entity from a record of the DB.
   *
   * @param row Is the tuple with the desired values of the new Fruit.
   * @return A new Fruit Object with the row data
   */
  public static Fruit from(Row row) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("#from(Row) - {}", row);
    }
    return new Fruit(row.getString("name"), row.getString("description"), row.getString("family"),
      row.getBoolean("ripen"));
  }

  @Override
  public String toString() {
    return "Fruit{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", family='" + family + '\'' + ", ripen=" + ripen + '}';
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public Boolean getRipen() {
    return ripen;
  }

  public void setRipen(Boolean ripen) {
    this.ripen = ripen;
  }
}