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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import com.sigpwned.picoxml.antlr4.XMLLexer;
import com.sigpwned.picoxml.antlr4.XMLParser;
import com.sigpwned.picoxml.listener.DefaultXmlParserListener;
import com.sigpwned.picoxml.util.XmlByteStreams;

/**
 * Reads as little into memory as possible
 */
public class StreamingXmlReader {
  public static XMLParser defaultXMLParser(Reader r) throws IOException {
    CharStream s = new UnbufferedCharStream(r);
    XMLLexer lexer = new XMLLexer(s);
    lexer.setTokenFactory(new CommonTokenFactory(true));
    TokenStream tokens = new UnbufferedTokenStream<>(lexer);
    XMLParser parser = new XMLParser(tokens);
    // parser.setBuildParseTree(false);
    parser.setTrimParseTree(true);

    return parser;
  }

  private final XMLParser parser;

  public StreamingXmlReader(File f) throws IOException {
    this(f, StandardCharsets.UTF_8);
  }

  public StreamingXmlReader(File f, Charset defaultCharset) throws IOException {
    this(new FileInputStream(f), defaultCharset);
  }

  public StreamingXmlReader(InputStream in) throws IOException {
    this(in, StandardCharsets.UTF_8);
  }

  public StreamingXmlReader(InputStream in, Charset defaultCharset) throws IOException {
    this(XmlByteStreams.decode(in, defaultCharset));
  }

  public StreamingXmlReader(String s) throws IOException {
    this(new StringReader(s));
  }

  public StreamingXmlReader(Reader r) throws IOException {
    this(defaultXMLParser(r));
  }

  protected StreamingXmlReader(XMLParser parser) {
    if (parser == null)
      throw new NullPointerException();
    this.parser = parser;
  }

  public void document(ContentHandler handler) {
    DefaultXmlParserListener listener = new DefaultXmlParserListener(handler);
    getParser().addParseListener(listener);
    try {
      getParser().document();
    } finally {
      getParser().removeParseListener(listener);
    }
  }

  private XMLParser getParser() {
    return parser;
  }
}
