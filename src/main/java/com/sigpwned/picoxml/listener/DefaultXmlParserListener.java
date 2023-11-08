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

import static java.util.Collections.emptySet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import org.antlr.v4.runtime.tree.TerminalNode;
import com.sigpwned.picoxml.ContentHandler;
import com.sigpwned.picoxml.Name;
import com.sigpwned.picoxml.antlr4.XMLLexer;
import com.sigpwned.picoxml.antlr4.XMLParser.CloseTagContext;
import com.sigpwned.picoxml.antlr4.XMLParser.DocumentContext;
import com.sigpwned.picoxml.antlr4.XMLParser.OpenCloseTagContext;
import com.sigpwned.picoxml.antlr4.XMLParser.OpenTagContext;
import com.sigpwned.picoxml.antlr4.XMLParserBaseListener;
import com.sigpwned.picoxml.model.Attribute;
import com.sigpwned.picoxml.model.Attributes;
import com.sigpwned.picoxml.model.node.CData;
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
public class DefaultXmlParserListener extends XMLParserBaseListener {
  private static class ElementNamespacing {
    public static ElementNamespacing of(boolean definesDefaultNamespace,
        Set<String> definesNamespacePrefixes) {
      return new ElementNamespacing(definesDefaultNamespace, definesNamespacePrefixes);
    }

    private final boolean definesDefaultNamespace;
    private final Set<String> definesNamespacePrefixes;

    public ElementNamespacing(boolean definesDefaultNamespace,
        Set<String> definesNamespacePrefixes) {
      this.definesDefaultNamespace = definesDefaultNamespace;
      this.definesNamespacePrefixes = definesNamespacePrefixes;
    }

    public Set<String> getDefinesNamespacePrefixes() {
      return definesNamespacePrefixes;
    }

    public boolean isDefinesDefaultNamespace() {
      return definesDefaultNamespace;
    }

    @Override
    public int hashCode() {
      return Objects.hash(definesDefaultNamespace, definesNamespacePrefixes);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ElementNamespacing other = (ElementNamespacing) obj;
      return definesDefaultNamespace == other.definesDefaultNamespace
          && Objects.equals(definesNamespacePrefixes, other.definesNamespacePrefixes);
    }

    @Override
    public String toString() {
      return "ElementNamespacing [definesDefaultNamespace=" + definesDefaultNamespace
          + ", definesNamespacePrefixes=" + definesNamespacePrefixes + "]";
    }
  }

  private final StringBuilder chbuf;
  private ContentHandler handler;

  public DefaultXmlParserListener(ContentHandler handler) {
    this();
    if (handler == null)
      throw new NullPointerException();
    this.handler = handler;
  }

  protected DefaultXmlParserListener() {
    this.chbuf = new StringBuilder();
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
  public void exitOpenTag(OpenTagContext ctx) {
    startElement(Name.fromString(ctx.Name().getText()), Mappings.attributes(ctx.attribute()));
  }

  @Override
  public void exitCloseTag(CloseTagContext ctx) {
    endElement(Name.fromString(ctx.Name().getText()));
  }

  @Override
  public void exitOpenCloseTag(OpenCloseTagContext ctx) {
    Name name = Name.fromString(ctx.Name().getText());
    startElement(name, Mappings.attributes(ctx.attribute()));
    endElement(name);
  }

  private void startElement(Name name, Attributes attributes) {
    ElementNamespacing namespacing = addNamespaces(attributes);

    pushElementNamespacing(namespacing);

    String namespace;
    if (name.isQualified()) {
      // TODO Should we report the unrecognized prefix?
      namespace = getPrefixNamespace(name.getPrefix()).orElse(null);
    } else {
      namespace = getDefaultNamespace().orElse(null);
    }

    handler.startElement(name.getPrefix(), name.getLocalName(), namespace, attributes);
  }

  private void endElement(Name name) {
    String namespace;
    if (name.isQualified()) {
      // TODO Should we report the unrecognized prefix?
      namespace = getPrefixNamespace(name.getPrefix()).orElse(null);
    } else {
      namespace = getDefaultNamespace().orElse(null);
    }

    handler.endElement(namespace, name.getLocalName(), name.toString());

    ElementNamespacing namespacing = popElementNamespacing();

    removeNamespaces(namespacing);
  }

  private ElementNamespacing addNamespaces(Attributes attributes) {
    boolean definesDefaultNamespace = false;
    Set<String> definesNamespacePrefixes = null;
    for (Attribute attribute : attributes) {
      Name attributeName = Name.fromString(attribute.getLocalName());
      if (attributeName.toString().equals("xmlns")) {
        // TODO Validate namespace?
        pushDefaultNamespace(attribute.getValue());
        definesDefaultNamespace = true;
      } else if (Objects.equals(attributeName.getPrefix(), "xmlns")) {
        // TODO Validate prefix?
        String namespacePrefix = attributeName.getLocalName();
        // TODO Validate namespace?
        String namespace = attribute.getValue();
        // TODO See if we already have prefix? See if we already have namespace?
        pushPrefixNamespace(namespacePrefix, namespace);
        handler.startPrefixMapping(namespacePrefix, namespace);
        if (definesNamespacePrefixes == null)
          definesNamespacePrefixes = new HashSet<>(4);
        definesNamespacePrefixes.add(namespacePrefix);
      }
    }
    return ElementNamespacing.of(definesDefaultNamespace,
        definesNamespacePrefixes == null ? emptySet() : definesNamespacePrefixes);
  }

  private void removeNamespaces(ElementNamespacing namespacing) {
    if (namespacing.isDefinesDefaultNamespace())
      popDefaultNamespace();
    for (String prefix : namespacing.getDefinesNamespacePrefixes())
      popPrefixNamespace(prefix);
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    switch (node.getSymbol().getType()) {
      case XMLLexer.CDATA:
        CData cdata = Mappings.cdata(node);
        handler.characters(cdata.getContent(), 0, cdata.getContent().length());
        break;
      case XMLLexer.PI:
        ProcessingInstruction processingInstruction = Mappings.processingInstruction(node);
        handler.processingInstruction(processingInstruction.getName(),
            processingInstruction.getContent());
        break;
      case XMLLexer.SEA_WS:
        WhiteSpace whitespace = Mappings.whitespace(node);
        handler.characters(whitespace.getContent(), 0, whitespace.getContent().length());
        break;
      case XMLLexer.TEXT:
        Text text = Mappings.text(node);
        handler.characters(text.getContent(), 0, text.getContent().length());
        break;
      case XMLLexer.EntityRef:
        EntityRef entityRef = Mappings.entityRef(node);
        Optional<String> maybeValue = StandardEntities.findStandardEntityValue(entityRef.getName());
        if (maybeValue.isPresent()) {
          String value = maybeValue.get();
          handler.characters(value, 0, value.length());
        } else {
          handler.skippedEntity(entityRef.getName());
        }
        break;
      case XMLLexer.CharRef:
        CharRef charRef = Mappings.charRef(node);
        int cp = Integer.parseInt(charRef.getDigits(), charRef.getBase());
        chbuf.setLength(0);
        chbuf.appendCodePoint(cp);
        handler.characters(chbuf, 0, chbuf.length());
        break;
      case XMLLexer.COMMENT:
        // Don't care.
        break;
      default:
        // These are all fine. Just ignore them.
        break;
    }
  }

  // ELEMENT NAMESPACING ////////////////////////////////////////////////////
  private Stack<ElementNamespacing> elements = new Stack<>();

  private void pushElementNamespacing(ElementNamespacing element) {
    elements.push(element);
  }

  private ElementNamespacing popElementNamespacing() {
    return elements.pop();
  }

  // DEFAULT NAMESPACE //////////////////////////////////////////////////////
  private Stack<String> defaultNamespaceStack = new Stack<>();

  private Optional<String> getDefaultNamespace() {
    return defaultNamespaceStack.isEmpty() ? Optional.empty()
        : Optional.of(defaultNamespaceStack.peek());
  }

  private void pushDefaultNamespace(String defaultNamespace) {
    defaultNamespaceStack.push(defaultNamespace);
  }

  private void popDefaultNamespace() {
    defaultNamespaceStack.pop();
  }

  // NAMESPACE PREFIXES /////////////////////////////////////////////////////
  private final Map<String, Stack<String>> namespacePrefixStacks = new HashMap<>();

  private Optional<String> getPrefixNamespace(String prefix) {
    return Optional.ofNullable(namespacePrefixStacks.get(prefix)).map(Stack::peek);
  }

  private void pushPrefixNamespace(String prefix, String namespace) {
    namespacePrefixStacks.computeIfAbsent(namespace, x -> new Stack<>()).push(namespace);
  }

  private void popPrefixNamespace(String prefix) {
    namespacePrefixStacks.compute(prefix, (x, xs) -> {
      xs.pop();
      return xs.isEmpty() ? null : xs;
    });
  }
}
