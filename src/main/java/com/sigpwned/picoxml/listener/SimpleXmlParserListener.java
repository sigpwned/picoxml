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
package com.sigpwned.picoxml.listener;

import java.util.Optional;
import org.antlr.v4.runtime.tree.TerminalNode;
import com.sigpwned.picoxml.ContentHandler;
import com.sigpwned.picoxml.antlr4.XMLLexer;
import com.sigpwned.picoxml.antlr4.XMLParser.DocumentContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ElementContext;
import com.sigpwned.picoxml.antlr4.XMLParserBaseListener;
import com.sigpwned.picoxml.model.node.CData;
import com.sigpwned.picoxml.model.node.Element;
import com.sigpwned.picoxml.model.node.ProcessingInstruction;
import com.sigpwned.picoxml.model.node.Text;
import com.sigpwned.picoxml.model.node.WhiteSpace;
import com.sigpwned.picoxml.model.node.reference.CharRef;
import com.sigpwned.picoxml.model.node.reference.EntityRef;
import com.sigpwned.picoxml.util.Mappings;
import com.sigpwned.picoxml.util.StandardEntities;

/**
 * Does not handle namespaces
 */
public class SimpleXmlParserListener extends XMLParserBaseListener {
  private final char[] chbuf;
  private ContentHandler handler;

  public SimpleXmlParserListener(ContentHandler handler) {
    this();
    if (handler == null)
      throw new NullPointerException();
    this.handler = handler;
  }

  protected SimpleXmlParserListener() {
    this.chbuf = new char[4];
  }

  protected void setHandler(ContentHandler newHandler) {
    if (handler != null)
      throw new IllegalStateException("handler already set");
    if (newHandler == null)
      throw new NullPointerException();
    this.handler = newHandler;
  }

  @Override
  public void enterDocument(DocumentContext ctx) {
    handler.startDocument();
  }

  @Override
  public void exitDocument(DocumentContext ctx) {
    handler.endDocument();
  }

  @Override
  public void enterElement(ElementContext ctx) {
    Element element = Mappings.element(ctx);
    String name = element.getName();
    handler.startElement("", name, "", Mappings.attributes(ctx.attribute()));
  }

  @Override
  public void exitElement(ElementContext ctx) {
    String name = Mappings.elementName(ctx);
    handler.endElement("", name, "");
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    switch (node.getSymbol().getType()) {
      case XMLLexer.CDATA:
        CData cdata = Mappings.cdata(node);
        handler.characters(cdata.getContent().toCharArray(), 0, cdata.getContent().length());
        break;
      case XMLLexer.PI:
        ProcessingInstruction processingInstruction = Mappings.processingInstruction(node);
        handler.processingInstruction(processingInstruction.getName(),
            processingInstruction.getContent());
        break;
      case XMLLexer.SEA_WS:
        WhiteSpace whitespace = Mappings.whitespace(node);
        handler.ignorableWhitespace(whitespace.getContent().toCharArray(), 0,
            whitespace.getContent().length());
        break;
      case XMLLexer.TEXT:
        Text text = Mappings.text(node);
        handler.characters(text.getContent().toCharArray(), 0, text.getContent().length());
        break;
      case XMLLexer.EntityRef:
        EntityRef entityRef = Mappings.entityRef(node);
        Optional<String> maybeValue = StandardEntities.findStandardEntityName(entityRef.getName());
        if (maybeValue.isPresent()) {
          String value = maybeValue.get();
          value.getChars(0, value.length(), chbuf, 0);
          handler.characters(chbuf, 0, value.length());
        } else {
          handler.skippedEntity(entityRef.getName());
        }
        break;
      case XMLLexer.CharRef:
        CharRef charRef = Mappings.charRef(node);
        int cp = Integer.parseInt(charRef.getDigits(), charRef.getBase());
        int len = Character.toChars(cp, chbuf, 0);
        handler.characters(chbuf, 0, len);
        break;
      case XMLLexer.COMMENT:
        // Don't care.
        break;
      default:
        // These are all fine. Just ignore them.
        break;
    }
  }
}
