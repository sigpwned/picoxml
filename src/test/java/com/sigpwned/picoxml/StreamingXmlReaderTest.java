/*-
 * =================================LICENSE_START==================================
 * picoxml
 * ====================================SECTION=====================================
 * Copyright (C) 2023 Andy Boothe
 * ====================================SECTION=====================================
 * This file is part of PicoXML 2 for Java.
 *
 * Copyright (C) 2000-2002 Marc De Scheemaecker, All Rights Reserved.
 * Copyright (C) 2020-2020 Saúl Hidalgo, All Rights Reserved.
 * Copyright (C) 2023-2023 Andy Boothe, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.picoxml;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.sigpwned.picoxml.model.Attributes;

public class StreamingXmlReaderTest {
  public class CollectingContentHandler implements ContentHandler {
    @Override
    public void startDocument() {
      events.add(format("startDocument()"));
    }

    @Override
    public void endDocument() {
      events.add(format("endDocument()"));
    }

    @Override
    public void characters(CharSequence cs, int off, int len) {
      events.add(format("characters(%s)", cs.subSequence(off, len).toString()));
    }

    @Override
    public void startElement(String prefix, String localName, String namespace, Attributes atts) {
      events.add(format("startElement(%s, %s, %s, %s)", prefix, localName, namespace, atts));
    }

    @Override
    public void endElement(String prefix, String localName, String namespace) {
      events.add(format("endElement(%s, %s, %s)", prefix, localName, namespace));
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
  };

  public List<String> events = new ArrayList<>();

  @Before
  public void setup() {
    events.clear();
  }

  @Test
  public void simpleTest() throws IOException {
    StreamingXmlReader r = new StreamingXmlReader(getClass().getResourceAsStream("simple.xml"),
        StandardCharsets.UTF_8);

    List<String> events = new ArrayList<>();
    r.document(new CollectingContentHandler());

    assertThat(events,
        is(asList("startDocument()", "characters(\n)",
            "processingInstruction(foobar,  This is a processing instruction. )", "characters(\n)",
            "startElement(null, greeting, greeting, [])", "characters(\n    )",
            "startElement(null, hello, hello, [])", "characters(world)",
            "endElement(null, hello, hello)", "characters(\n    )", "characters(\n    )",
            "startElement(null, entities, entities, [])", "characters(<)", "characters( )",
            "characters(>)", "characters( )", "characters(')", "characters( )", "characters(\")",
            "characters( )", "characters(A)", "characters( )", "characters(A)",
            "endElement(null, entities, entities)", "characters(\n    )",
            "startElement(null, selfclosed, selfclosed, [])",
            "endElement(null, selfclosed, selfclosed)", "characters(\n    )",
            "characters( This is CDATA. )", "characters(\n    This is chardata.\n)",
            "endElement(null, greeting, greeting)", "endDocument()")));
  }

  @Test
  public void namespaceTest() throws IOException {
    StreamingXmlReader r = new StreamingXmlReader(getClass().getResourceAsStream("simple.xml"),
        StandardCharsets.UTF_8);

    List<String> events = new ArrayList<>();
    r.document(new CollectingContentHandler());

    assertThat(events,
        is(asList("startDocument()", "characters(\n)",
            "processingInstruction(foobar,  This is a processing instruction. )", "characters(\n)",
            "startElement(null, greeting, greeting, [])", "characters(\n    )",
            "startElement(null, hello, hello, [])", "characters(world)",
            "endElement(null, hello, hello)", "characters(\n    )", "characters(\n    )",
            "startElement(null, entities, entities, [])", "characters(<)", "characters( )",
            "characters(>)", "characters( )", "characters(')", "characters( )", "characters(\")",
            "characters( )", "characters(A)", "characters( )", "characters(A)",
            "endElement(null, entities, entities)", "characters(\n    )",
            "startElement(null, selfclosed, selfclosed, [])",
            "endElement(null, selfclosed, selfclosed)", "characters(\n    )",
            "characters( This is CDATA. )", "characters(\n    This is chardata.\n)",
            "endElement(null, greeting, greeting)", "endDocument()")));
  }
}
