package com.orientechnologies.gog.domain;

/**
 * Created by luigidellaquila on 25/05/16.
 */
public class Relationship {
  public String from;
  public String to;
  public String type;
  public String rawData;

  public Relationship(String from, String to, String type, String rawData) {
    this.type = type;
    this.from = from;
    this.to = to;
    this.rawData = rawData;
  }
}
