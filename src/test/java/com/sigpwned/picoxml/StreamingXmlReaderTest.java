package com.sigpwned.picoxml;

import static java.lang.String.format;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.sigpwned.picoxml.model.Attributes;

public class StreamingXmlReaderTest {
  @Test
  public void test() throws IOException {
    StreamingXmlReader r = new StreamingXmlReader(getClass().getResourceAsStream("simple.xml"),
        StandardCharsets.UTF_8);

    List<String> events = new ArrayList<>();
    r.document(new ContentHandler() {
      @Override
      public void startDocument() {
        events.add(format("startDocument()"));
      }

      @Override
      public void endDocument() {
        events.add(format("endDocument()"));
      }

      @Override
      public void ignorableWhitespace(char[] ch, int start, int length) {
        events.add(format("ignorableWhitespace(%s)", new String(ch, start, length)));
      }

      @Override
      public void characters(char[] ch, int start, int length) {
        events.add(format("characters(%s)", new String(ch, start, length)));
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes atts) {
        events.add(format("startElement(%s, %s, %s, %s)", uri, localName, qName, atts));
      }

      @Override
      public void endElement(String uri, String localName, String qName) {
        events.add(format("endElement(%s, %s, %s)", uri, localName, qName));
      }

      @Override
      public void processingInstruction(String target, String data) {
        events.add(format("processingInstruction(%s, %s)", target, data));
      }

      @Override
      public void skippedEntity(String name) {
        events.add(format("skippedEntity(%s)", name));
      }

      @Override
      public void startPrefixMapping(String prefix, String uri) {
        events.add(format("startPrefixMapping(%s, %s)", prefix, uri));
      }

      @Override
      public void endPrefixMapping(String prefix) {
        events.add(format("endPrefixMapping(%s)", prefix));
      }
    });

    System.out.println(events);
  }
}
