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
import com.sigpwned.picoxml.ContentHandler;
import com.sigpwned.picoxml.model.Attribute;
import com.sigpwned.picoxml.model.Attributes;

public class NamespaceAwareXmlParserListener extends SimpleXmlParserListener {
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

  private class NamespaceMappingContentHandler implements ContentHandler {
    @Override
    public void startDocument() {
      delegate.startDocument();
    }

    @Override
    public void endDocument() {
      delegate.endDocument();
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
      delegate.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      delegate.characters(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
      Set<String> definesNamespacePrefixes = null;
      boolean definesDefaultNamespace = false;
      for (Attribute att : atts) {
        if (att.getName().equals("xmlns")) {
          // TODO Validate namespace?
          pushDefaultNamespace(att.getValue());
          definesDefaultNamespace = true;
        } else if (att.getName().startsWith("xmlns:")) {
          // TODO Validate prefix?
          String namespacePrefix = att.getName().substring(6, att.getName().length());
          // TODO Validate namespace?
          String namespace = att.getValue();
          // TODO See if we already have prefix? See if we already have namespace?
          pushPrefixNamespace(namespacePrefix, namespace);
          delegate.startPrefixMapping(namespacePrefix, namespace);
          if (definesNamespacePrefixes == null)
            definesNamespacePrefixes = new HashSet<>(4);
          definesNamespacePrefixes.add(namespacePrefix);
        }
      }

      pushElementNamespacing(ElementNamespacing.of(definesDefaultNamespace,
          definesNamespacePrefixes == null ? emptySet() : definesNamespacePrefixes));

      final int colon = localName.indexOf(":");
      if (colon != -1) {
        // So this is clearly a qname
        final String qname = localName;
        // TODO What if prefix is empty?
        final String prefix = qname.substring(0, colon);
        // TODO What if name is empty?
        final String name = qname.substring(colon + 1, localName.length());
        // TODO What if prefix does not exist?
        final String namespace = getPrefixNamespace(prefix).orElse("");
        delegate.startElement(namespace, name, localName, atts);
      } else {
        // TODO Should we have empty strings?
        final String defaultNamespace = getDefaultNamespace().orElse("");
        delegate.startElement(defaultNamespace, localName, "", atts);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      final int colon = localName.indexOf(":");
      if (colon != -1) {
        // So this is clearly a qname
        final String qname = localName;
        // TODO What if prefix is empty?
        final String prefix = qname.substring(0, colon);
        // TODO What if name is empty?
        final String name = qname.substring(colon + 1, localName.length());
        // TODO What if prefix does not exist?
        final String namespace = getPrefixNamespace(prefix).orElse("");
        delegate.endElement(namespace, name, qname);
      } else {
        // TODO Should we have empty strings?
        final String defaultNamespace = getDefaultNamespace().orElse("");
        delegate.endElement(defaultNamespace, localName, "");
      }

      final ElementNamespacing elementNamespacing = popElementNamespacing();

      if (elementNamespacing.isDefinesDefaultNamespace())
        popDefaultNamespace();

      for (String namespacePrefix : elementNamespacing.getDefinesNamespacePrefixes()) {
        popPrefixNamespace(namespacePrefix);
        delegate.endPrefixMapping(namespacePrefix);
      }
    }

    @Override
    public void processingInstruction(String target, String data) {
      delegate.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) {
      delegate.skippedEntity(name);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
      // This will never be called, by definition.
      throw new UnsupportedOperationException();
    }

    @Override
    public void endPrefixMapping(String prefix) {
      // This will never be called, by definition.
      throw new UnsupportedOperationException();
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

  private final ContentHandler delegate;

  public NamespaceAwareXmlParserListener(ContentHandler handler) {
    setHandler(new NamespaceMappingContentHandler());
    this.delegate = handler;
  }
}
