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
import com.tinkerpop.blueprints.Graph;

/**
 * @author Luigi Dell'Aquila
 */
public interface DBImporter {

  /**
   * Creates a vertex in the DB starting
   * @param currentElement The element that contains info about the vertex
   */
  public void createVertex(Element currentElement);

  /**
   * Create an edge starting from a Relationship object
   * @param rel The Relationship object that contains information about the edge that is being created
   * @param edgeClass The edge class/label
   * @param directionMatters if set to true, the method will avoid to create a relationship A -E-> B if an edge A <-E- B of the same type already exists
   * @return true if the edge was created, false otherwise. An edge created only if both vertices exist
   */
  public boolean createEdge(Relationship rel, String edgeClass, boolean directionMatters);

  /**
   * @return the graph database instance used by the importer
   */
  public Graph getGraph();

  /**
   * Closes this importer instance (it also closes the graph connection)
   */
  public void close();

}
