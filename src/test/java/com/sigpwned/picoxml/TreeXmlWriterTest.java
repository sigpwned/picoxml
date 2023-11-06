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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
  public void test() throws IOException {
    CharStream s = CharStreams.fromStream(getClass().getResourceAsStream("simple.xml"),
        StandardCharsets.UTF_8);
    XMLLexer lexer = new XMLLexer(s);
    TokenStream tokens = new CommonTokenStream(lexer);
    XMLParser parser = new XMLParser(tokens);
    TreeXmlReader p = new TreeXmlReader(parser);

    Document doc = p.document();

    System.out.println("before");

    Writer w = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
    try {
      new TreeXmlWriter(w).write(doc);
    } finally {
      w.flush();
    }

    System.out.println("after");
  }
}
