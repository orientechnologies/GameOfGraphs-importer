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
package com.orientechnoloties.gog;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.orientechnologies.gog.GameOfThronesWikiParser;
import com.orientechnologies.gog.OrientDBImporter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 * Author Luigi Dell'Aquila
 */
public class SimpleOrientDBTest {

  @Test public void test() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();

    XMLReader xmlReader = saxParser.getXMLReader();
    //    OrientDBImporter importer = new OrientDBImporter("remote:localhost/GameOfGraphs", "admin", "admin");
    OrientDBImporter importer = new OrientDBImporter("memory:GameOfGraphs", "admin", "admin");
    xmlReader.setContentHandler(new GameOfThronesWikiParser(importer));
    xmlReader.parse(String.valueOf(SimpleOrientDBTest.class.getClassLoader().getResource("gameOfThrones.xml").toURI()));

    OrientGraph graph = importer.getGraph();

    Iterable<Vertex> vertices = graph.getVertices("Character.name", "Eddard Stark");
    Iterator<Vertex> iterator = vertices.iterator();
    Assert.assertTrue(iterator.hasNext());
    Vertex ned = iterator.next();
    Assert.assertEquals(ned.getProperty("name"), "Eddard Stark");
    Iterable<Vertex> family = ned.getVertices(Direction.BOTH, "Has_Family");
    boolean found = false;
    for (Vertex v : family) {
      if ("Jon Snow".equals(v.getProperty("name"))) {
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);
    importer.close();
  }

}
