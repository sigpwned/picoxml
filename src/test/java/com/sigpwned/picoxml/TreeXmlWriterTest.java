/*-
 * =================================LICENSE_START==================================
 * picoxml
 * ====================================SECTION=====================================
 * Copyright (C) 2023 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.picoxml;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import com.sigpwned.picoxml.antlr4.XMLLexer;
import com.sigpwned.picoxml.antlr4.XMLParser;
import com.sigpwned.picoxml.model.Document;

public class TreeXmlWriterTest {
  @Test
  public void simpleTest() throws IOException {
    CharStream s = CharStreams.fromStream(getClass().getResourceAsStream("simple.xml"),
        StandardCharsets.UTF_8);
    XMLLexer lexer = new XMLLexer(s);
    TokenStream tokens = new CommonTokenStream(lexer);
    XMLParser parser = new XMLParser(tokens);
    XmlReader p = new XmlReader(parser);

    Document doc = p.document();

    StringWriter w = new StringWriter();
    try {
      new XmlWriter(w).document(doc);
    } finally {
      w.close();
    }

    assertThat(w.toString(),
        is("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<?foobar This is a processing instruction. ?>\n"
            + "<greeting alpha=\"bravo\" test=\"1 &lt; 2\">\n" + "    <hello>world</hello>\n"
            + "    <!-- This is a comment -->\n"
            + "    <entities>&lt; &gt; &apos; &quot; &#65; &#x41;</entities>\n"
            + "    <selfclosed name=\"value\" />\n" + "    <!CDATA[[ This is CDATA. ]]>\n"
            + "    This is chardata.\n" + "</greeting>"));
  }

  @Test
  public void namespacesTest() throws IOException {
    CharStream s = CharStreams.fromStream(getClass().getResourceAsStream("namespaces.xml"),
        StandardCharsets.UTF_8);
    XMLLexer lexer = new XMLLexer(s);
    TokenStream tokens = new CommonTokenStream(lexer);
    XMLParser parser = new XMLParser(tokens);
    XmlReader p = new XmlReader(parser);

    Document doc = p.document();

    StringWriter w = new StringWriter();
    try {
      new XmlWriter(w).document(doc);
    } finally {
      w.close();
    }

    assertThat(w.toString(),
        is("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<alpha xmlns=\"https://www.example.com/default1\">\n" + "    <bravo />\n"
            + "    <charlie xmlns:x=\"https://www.example.com/x\">\n"
            + "        <x:delta a=\"b\" x:c=\"d\" />\n" + "    </charlie>\n"
            + "    <echo xmlns=\"https://www.example.com/default2\">\n" + "        <foxtrot />\n"
            + "    </echo>\n" + "</alpha>"));
  }
}
