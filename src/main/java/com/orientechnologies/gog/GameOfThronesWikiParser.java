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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.orientechnologies.gog.domain.Element;
import com.orientechnologies.gog.domain.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Luigi Dell'Aquila
 */
public class GameOfThronesWikiParser extends DefaultHandler {

  private String lastXmlElement = null;

  private Element currentElement;
  private String  allText;
  private List<Relationship> relationships = new ArrayList<>();
  private DBImporter dbImporter;


  public GameOfThronesWikiParser(DBImporter dbImporter) {
    this.dbImporter = dbImporter;
  }

  @Override public void startDocument() throws SAXException {
  }

  @Override public void endDocument() throws SAXException {
    for (Relationship rel : relationships) {
      String edgeClass = GraphConventions.toEdgeClass(rel);
      if (GraphConventions.excludeEdgeClasses.contains(edgeClass)) {
        continue;
      }
      boolean directionMatters = directionMatters(edgeClass);
      if (dbImporter.createEdge(rel, edgeClass, directionMatters)) {
      }
    }
  }

  private boolean directionMatters(String edgeClass) {
    return !GraphConventions.undirectedEdgeClasses.contains(edgeClass);
  }

  @Override public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("page".equals(localName)) {
      currentElement = new Element();
    }
    allText = "";
    lastXmlElement = localName;
  }

  @Override public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("page".equals(localName)) {
      saveCurrentElement();
    }
    if ("title".equals(lastXmlElement)) {
      if (!allText.contains(":")) {
        currentElement.name = allText;
      }
    } else if ("text".equals(lastXmlElement)) {
      String text = allText;
      if (text.contains("{{")) {
        text = text.substring(text.indexOf("{{"));
        while (true) {
          if (text.startsWith("{{Quote") || text.startsWith("{{Heraldry")) {
            text = text.substring(text.indexOf("}}") + 2).trim();
            continue;
          }
          if (text.startsWith("{{")) {
            parseMarkDown(text.substring(2, Math.max(text.indexOf("}}"), 2)));
          }
          break;
        }
      }
    }
    lastXmlElement = null;
  }

  private void saveCurrentElement() {
    dbImporter.createVertex(currentElement);
    for (Map.Entry<String, List<Relationship>> entry : currentElement.relationships.entrySet()) {
      List<Relationship> relationships = entry.getValue();
      for (Relationship rel : relationships) {
        this.relationships.add(rel);
      }
    }
  }

  @Override public void characters(char[] ch, int start, int length) throws SAXException {
    allText += toString(ch, start, length);
  }

  private void parseMarkDown(String text) {
    String cat = text.substring(0, Math.max(text.indexOf("|"), 0)).replaceAll("infobox", "").replaceAll("Infobox", "")
        .replaceAll("Template:", "").trim();
    if (cat.length() > 0) {
      currentElement.category = cat.replaceAll(" ", "");
    }

    List<String> items = splitItems(text);
    for (String item : items) {
      String[] splitted = item.split("=");
      if (splitted.length == 2) {
        splitted[0] = splitted[0].replaceAll("\n", " ").replaceAll("\t", " ").trim();
        splitted[1] = splitted[1].replaceAll("\n", " ").replaceAll("\t", " ").trim();
        if (isRelationship(splitted[1])) {
          currentElement.relationships.put(splitted[0], extractRelationships(currentElement.name, splitted[0], splitted[1]));
        } else if (splitted[1].length() > 0) {
          currentElement.attributes.put(splitted[0], splitted[1]);
        }
      }
    }
  }

  private List<Relationship> extractRelationships(String from, String type, String text) {
    List<Relationship> result = new ArrayList<>();
    int inSq = 0;

    String buffer = "";
    for (char c : text.toCharArray()) {
      if (c == '[') {
        inSq++;
      } else if (c == ']') {
        inSq--;
        if (inSq == 0) {
          buffer = buffer.split("\\|")[0];
          if (!buffer.contains(":")) {
            Relationship rel = new Relationship(from, buffer, type, text);
            result.add(rel);
          }
          buffer = "";
        }
      } else if (inSq == 2) {
        buffer += c;
      }

    }
    return result;

  }

  private boolean isRelationship(String s) {
    return s.contains("[[");
  }

  private List<String> splitItems(String text) {
    List<String> result = new ArrayList<>();
    boolean inHtmlS = false;
    int inSq = 0;

    String buffer = "";
    for (char c : text.toCharArray()) {
      if (inHtmlS) {
        if (c == ';') {
          inHtmlS = false;
        }
      } else if (c == '&') {
        inHtmlS = true;
      } else if (c == '[') {
        buffer += c;
        inSq++;
      } else if (c == ']') {
        buffer += c;
        inSq--;
      } else if (inSq == 0 && c == '|') {
        result.add(buffer);
        buffer = "";
      } else {
        buffer += c;
      }

    }
    return result;
  }

  private String toString(char[] ch, int start, int length) {
    String text = new String(ch);
    text = text.substring(start, start + length);
    return text.trim();
  }

}
