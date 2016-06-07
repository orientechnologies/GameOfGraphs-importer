/*
 *
 *    Copyright 2016 OrientDB LTD  - info(at)orientdb.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.orientechnologies.gog;

import com.orientechnologies.gog.domain.Element;
import com.orientechnologies.gog.domain.Relationship;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Luigi Dell'Aquila
 */
public class OrientDBImporter implements DBImporter {

  OrientGraph graph;

  public OrientDBImporter(String url, String username, String password) {
    graph = new OrientGraph(url, username, password);
  }

  public void createVertex(Element currentElement) {
    if (currentElement.name != null && currentElement.category != null && currentElement.category.trim().length() > 0) {
      OrientVertex v = graph.addVertex("class:" + currentElement.category);
      v.setProperty("name", currentElement.name);
      v.setProperty("url", currentElement.getUrl());
      for (Map.Entry<String, String> entry : currentElement.attributes.entrySet()) {
        v.setProperty(normalizeAttrName(entry.getKey()), entry.getValue());
      }
      graph.commit();
    }
  }

  public boolean createEdge(Relationship rel, String edgeClass, boolean directionMatters) {
    OrientVertex from = loadVertex(rel.from);
    OrientVertex to = loadVertex(rel.to);
    if (from == null) {
      return false;
    }
    if (to == null) {
      return false;
    }

    if (directionMatters || !thereIsEdge(from, to, edgeClass)) {
      Edge edge = from.addEdge(edgeClass, to);
      edge.setProperty("raw", rel.rawData);
      graph.commit();
      return true;

    }
    return false;
  }

  public OrientGraph getGraph() {
    return graph;
  }

  public void close() {
    graph.shutdown();
  }

  private boolean thereIsEdge(OrientVertex from, OrientVertex to, String edgeClass) {
    for (Vertex v : from.getVertices(Direction.BOTH, edgeClass)) {
      if (v.equals(to)) {
        return true;
      }
    }
    return false;
  }

  private OrientVertex loadVertex(String rel) {
    Iterable<Vertex> matches = graph.getVertices("V.name", rel);
    Iterator<Vertex> iterator = matches.iterator();
    if (iterator.hasNext()) {
      return (OrientVertex) iterator.next();
    }
    return null;
  }

  private String normalizeAttrName(String key) {
    key = key.replaceAll(" ", "");
    key = key.replaceAll("\\.", "");
    key = key.replaceAll(";", "");
    key = key.replaceAll(",", "");
    return key;
  }
}
