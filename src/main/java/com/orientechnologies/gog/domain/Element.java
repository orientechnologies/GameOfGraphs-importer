package com.orientechnologies.gog.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luigidellaquila on 25/05/16.
 */
public class Element {
  /**
   * The element type/cathegory/class
   */
  public String category;

  /**
   * The name of the element. It is used as a unique identifier in the graph
   */
  public String name;

  /**
   * The outgoing relationships from this node
   */
  public Map<String, List<Relationship>> relationships = new HashMap<>();

  /**
   * The node attributes (key/value)
   */
  public Map<String, String>       attributes    = new HashMap<>();

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(name);
    builder.append("\n");
    builder.append("\tCategory: " + category + "\n");
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      builder.append("\t" + entry.getKey() + ": " + entry.getValue() + "\n");
    }
    for (Map.Entry<String, List<Relationship>> entry : relationships.entrySet()) {
      builder.append("\t" + entry.getKey() + ": " + entry.getValue() + "\n");
    }
    return builder.toString();
  }

  /**
   *
   * @return The external resource URL for this element (if it exists)
   */
  public String getUrl() {
    return "http://gameofthrones.wikia.com/wiki/" + name;
  }
}
