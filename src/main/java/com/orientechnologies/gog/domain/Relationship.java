package com.orientechnologies.gog.domain;

/**
 * Created by luigidellaquila on 25/05/16.
 */
public class Relationship {
  /**
   * The unique identifier (name) of the starting point node
   */
  public String from;

  /**
   * The unique identifier (name) of the end point node
   */
  public String to;

  /**
   * The label/class/type of the relationship
   */
  public String type;

  /**
   * The raw description of the relationship (plain text)
   */
  public String rawData;

  /**
   *
   * @param from The unique identifier (name) of the starting point nodeThe unique identifier (name) of the starting point node
   * @param to The unique identifier (name) of the end point node
   * @param type The label/class/type of the relationship
   * @param rawData The raw description of the relationship (plain text)
   */
  public Relationship(String from, String to, String type, String rawData) {
    this.type = type;
    this.from = from;
    this.to = to;
    this.rawData = rawData;
  }
}
