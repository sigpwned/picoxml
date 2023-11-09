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
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import com.sigpwned.picoxml.antlr4.XMLLexer;
import com.sigpwned.picoxml.antlr4.XMLParser;
import com.sigpwned.picoxml.model.Document;

public class TreeXmlReaderTest {
  @Test
  public void simpleTest() throws IOException {
    CharStream s = CharStreams.fromStream(getClass().getResourceAsStream("simple.xml"),
        StandardCharsets.UTF_8);
    XMLLexer lexer = new XMLLexer(s);
    TokenStream tokens = new CommonTokenStream(lexer);
    XMLParser parser = new XMLParser(tokens);
    XmlReader p = new XmlReader(parser);

    Document doc = p.document();

    assertThat(doc.toString(), is(
        "Document [prolog=Prolog [declaration=XmlDeclaration [attributes=[Attribute [prefix=null, localName=version, value=1.0, namespace=null], Attribute [prefix=null, localName=encoding, value=UTF-8, namespace=null]]]], beforeMiscs=[WhiteSpace [content=\n], ProcessingInstruction [name=foobar, content= This is a processing instruction. ], WhiteSpace [content=\n]], root=Element [prefix=null, localName=greeting, attributes=[Attribute [prefix=null, localName=alpha, value=bravo, namespace=null], Attribute [prefix=null, localName=test, value=1 < 2, namespace=null]], namespace=null, children=[WhiteSpace [content=\n    ], Element [prefix=null, localName=hello, attributes=[], namespace=null, children=[Text [content=world]]], WhiteSpace [content=\n    ], Comment [content= This is a comment ], WhiteSpace [content=\n    ], Element [prefix=null, localName=entities, attributes=[], namespace=null, children=[EntityReference [name=lt], WhiteSpace [content= ], EntityReference [name=gt], WhiteSpace [content= ], EntityReference [name=apos], WhiteSpace [content= ], EntityReference [name=quot], WhiteSpace [content= ], CharRef [base=10, digits=65], WhiteSpace [content= ], CharRef [base=16, digits=41]]], WhiteSpace [content=\n    ], Element [prefix=null, localName=selfclosed, attributes=[Attribute [prefix=null, localName=name, value=value, namespace=null]], namespace=null, children=[]], WhiteSpace [content=\n    ], CData [content= This is CDATA. ], Text [content=\n    This is chardata.\n]]], afterMiscs=[]]"));
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

    assertThat(doc.toString(), is(
        "Document [prolog=Prolog [declaration=XmlDeclaration [attributes=[Attribute [prefix=null, localName=version, value=1.0, namespace=null], Attribute [prefix=null, localName=encoding, value=UTF-8, namespace=null]]]], beforeMiscs=[WhiteSpace [content=\n]], root=Element [prefix=null, localName=alpha, attributes=[Attribute [prefix=null, localName=xmlns, value=https://www.example.com/default1, namespace=null]], namespace=https://www.example.com/default1, children=[WhiteSpace [content=\n    ], Element [prefix=null, localName=bravo, attributes=[], namespace=https://www.example.com/default1, children=[]], WhiteSpace [content=\n    ], Element [prefix=null, localName=charlie, attributes=[Attribute [prefix=xmlns, localName=x, value=https://www.example.com/x, namespace=null]], namespace=https://www.example.com/default1, children=[WhiteSpace [content=\n        ], Element [prefix=x, localName=delta, attributes=[Attribute [prefix=null, localName=a, value=b, namespace=null], Attribute [prefix=x, localName=c, value=d, namespace=https://www.example.com/x]], namespace=https://www.example.com/x, children=[]], WhiteSpace [content=\n    ]]], WhiteSpace [content=\n    ], Element [prefix=null, localName=echo, attributes=[Attribute [prefix=null, localName=xmlns, value=https://www.example.com/default2, namespace=null]], namespace=https://www.example.com/default2, children=[WhiteSpace [content=\n        ], Element [prefix=null, localName=foxtrot, attributes=[], namespace=https://www.example.com/default2, children=[]], WhiteSpace [content=\n    ]]], WhiteSpace [content=\n]]], afterMiscs=[]]"));
  }
}
