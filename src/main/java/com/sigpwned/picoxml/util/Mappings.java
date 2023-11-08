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
package com.sigpwned.picoxml.util;

import static java.util.stream.Collectors.toList;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.tree.TerminalNode;
import com.sigpwned.picoxml.Name;
import com.sigpwned.picoxml.antlr4.XMLParser.AttributeContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ChardataContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ContentContext;
import com.sigpwned.picoxml.antlr4.XMLParser.DocumentContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ElementContext;
import com.sigpwned.picoxml.antlr4.XMLParser.MiscContext;
import com.sigpwned.picoxml.antlr4.XMLParser.PrologContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ReferenceContext;
import com.sigpwned.picoxml.model.Attribute;
import com.sigpwned.picoxml.model.Attributes;
import com.sigpwned.picoxml.model.Document;
import com.sigpwned.picoxml.model.Misc;
import com.sigpwned.picoxml.model.Miscs;
import com.sigpwned.picoxml.model.Node;
import com.sigpwned.picoxml.model.Nodes;
import com.sigpwned.picoxml.model.Prolog;
import com.sigpwned.picoxml.model.XmlDeclaration;
import com.sigpwned.picoxml.model.node.CData;
import com.sigpwned.picoxml.model.node.Comment;
import com.sigpwned.picoxml.model.node.Element;
import com.sigpwned.picoxml.model.node.ProcessingInstruction;
import com.sigpwned.picoxml.model.node.Ref;
import com.sigpwned.picoxml.model.node.Text;
import com.sigpwned.picoxml.model.node.WhiteSpace;
import com.sigpwned.picoxml.model.node.reference.CharRef;
import com.sigpwned.picoxml.model.node.reference.EntityRef;

public final class Mappings {
  private Mappings() {}

  public static Document document(DocumentContext c) {
    final Prolog prolog;
    if (c.prolog() != null)
      prolog = prolog(c.prolog());
    else
      prolog = null;

    final List<Map.Entry<Integer, Misc>> miscs =
        c.misc().stream().map(ci -> new SimpleImmutableEntry<>(ci.getSourceInterval().a, misc(ci)))
            .sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(toList());

    final int divider = c.element().getSourceInterval().a;

    Miscs beforeMiscs = new Miscs(miscs.stream().filter(m -> m.getKey() < divider)
        .map(Map.Entry::getValue).collect(toList()));

    Miscs afterMiscs = new Miscs(miscs.stream().filter(m -> m.getKey() > divider)
        .map(Map.Entry::getValue).collect(toList()));

    final Element root = element(c.element());

    return new Document(prolog, beforeMiscs, root, afterMiscs);
  }

  public static Misc misc(MiscContext c) {
    if (c.COMMENT() != null) {
      return comment(c.COMMENT());
    }
    if (c.PI() != null) {
      return processingInstruction(c.PI());
    }
    if (c.SEA_WS() != null) {
      return whitespace(c.SEA_WS());
    }
    throw new AssertionError(c);
  }

  public static String elementName(ElementContext c) {
    if (c.openTag() != null)
      return c.openTag().Name().getText();
    if (c.openCloseTag() != null)
      return c.openCloseTag().Name().getText();
    throw new AssertionError(c);
  }

  public static Element element(ElementContext c) {
    if (c.openTag() != null && c.closeTag() != null) {
      Name name = Name.fromString(c.openTag().Name().getText());
      Attributes attributes = attributes(c.openTag().attribute());
      // Open and closing tag
      // TODO Should we check the name on the close tag?
      List<Map.Entry<Integer, Node>> children = new ArrayList<>();

      ContentContext content = c.content();
      content.CDATA().stream()
          .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, cdata(n)))
          .forEach(children::add);
      content.chardata().stream()
          .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, chardata(n)))
          .forEach(children::add);
      content.COMMENT().stream()
          .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, comment(n)))
          .forEach(children::add);
      content.element().stream()
          .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, element(n)))
          .forEach(children::add);
      content.PI().stream()
          .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a,
              processingInstruction(n)))
          .forEach(children::add);
      content.reference().stream()
          .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, ref(n)))
          .forEach(children::add);

      children.sort(Comparator.comparingInt(Map.Entry::getKey));

      Nodes nodes = new Nodes(children.stream().map(Map.Entry::getValue).collect(toList()));

      return new Element(nodes, name.getPrefix(), name.getLocalName(), attributes);
    }
    if (c.openCloseTag() != null) {
      Name name = Name.fromString(c.openCloseTag().Name().getText());
      Attributes attributes = attributes(c.openCloseTag().attribute());
      return new Element(Nodes.EMPTY, name.getPrefix(), name.getLocalName(), attributes);
    }
    throw new AssertionError(c);
  }

  public static CData cdata(TerminalNode n) {
    // SYNTAX: <!CDATA[[$CONTENT]]>
    String text = n.getText();
    return new CData(text.substring(9, text.length() - 3));
  }

  public static Text chardata(ChardataContext c) {
    return new Text(c.getText());
  }

  public static WhiteSpace whitespace(TerminalNode n) {
    return new WhiteSpace(n.getText());
  }

  public static Text text(TerminalNode n) {
    return new Text(n.getText());
  }

  public static Comment comment(TerminalNode n) {
    // SYNTAX: <!--$CONTENT-->
    String text = n.getText();
    return new Comment(text.substring(4, text.length() - 3));
  }

  public static ProcessingInstruction processingInstruction(TerminalNode n) {
    // SYNTAX: <?$NAME$CONTENT>
    final String text = n.getText();

    int wsi = 2;
    while (wsi < text.length()) {
      final char ch = text.charAt(wsi);
      if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')
        break;
      else
        wsi = wsi + 1;
    }

    final String name = text.substring(2, wsi);
    final String content = text.substring(wsi, text.length() - 2);

    return new ProcessingInstruction(name, content);
  }

  public static Ref ref(ReferenceContext c) {
    if (c.EntityRef() != null) {
      return entityRef(c.EntityRef());
    }
    if (c.CharRef() != null) {
      return charRef(c.CharRef());
    }
    throw new AssertionError(c);
  }

  public static CharRef charRef(TerminalNode n) {
    String text = n.getText();
    if (text.startsWith("&#x")) {
      // SYNTAX: &#x$HEXDIGITS;
      return new CharRef(CharRef.HEX, text.substring(3, text.length() - 1));
    } else {
      // SYNTAX: &#$DIGITS;
      return new CharRef(CharRef.DEC, text.substring(2, text.length() - 1));
    }
  }

  public static EntityRef entityRef(TerminalNode n) {
    // SYNTAX: &$NAME;
    String text = n.getText();
    return new EntityRef(text.substring(1, text.length() - 1));
  }

  public static Prolog prolog(PrologContext c) {
    Attributes attributes = attributes(c.attribute());
    return new Prolog(new XmlDeclaration(attributes));
  }

  public static Attributes attributes(List<AttributeContext> cs) {
    return Attributes.of(cs.stream().map(Mappings::attribute).collect(toList()));
  }

  public static Attribute attribute(AttributeContext c) {
    // TODO Should this have an EntityResolver?
    Name name = Name.fromString(c.Name().getText());
    String string = c.STRING().getText();
    String value = XmlStrings.unescape(string.substring(1, string.length() - 1));
    return new Attribute(name.getPrefix(), name.getLocalName(), value);
  }
}
