/*
 *
 *    Copyright 2016 OrientDB LTD - info(at)orientdb.com
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

import com.orientechnologies.gog.domain.Relationship;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Luigi Dell'Aquila
 */
public class GraphConventions {

  public static final Set<String> undirectedEdgeClasses = Collections
      .unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "Has_Family" })));

  public static final Set<String> excludeEdgeClasses = Collections
      .unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "Has_result", "Has_actor" })));

  public static String toEdgeClass(Relationship rel) {
    if (rel.type.equals("Owner")) {
      return "Owner";
    }
    if (rel.type.equals("actor")) {
      return "Starring";
    }
    if (rel.type.equals("Death")) {
      return "KilledBy";
    }
    return "Has_" + rel.type;
  }
}
