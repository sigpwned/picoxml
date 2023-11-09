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

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;
import com.sigpwned.picoxml.antlr4.XMLLexer;
import com.sigpwned.picoxml.antlr4.XMLParser;
import com.sigpwned.picoxml.antlr4.XMLParser.AttributeContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ContentContext;
import com.sigpwned.picoxml.antlr4.XMLParser.DocumentContext;
import com.sigpwned.picoxml.antlr4.XMLParser.ElementContext;
import com.sigpwned.picoxml.antlr4.XMLParser.MiscContext;
import com.sigpwned.picoxml.antlr4.XMLParser.PrologContext;
import com.sigpwned.picoxml.antlr4.XMLParserBaseListener;
import com.sigpwned.picoxml.exception.InvalidSyntaxXmlException;
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
import com.sigpwned.picoxml.model.node.Text;
import com.sigpwned.picoxml.model.node.WhiteSpace;
import com.sigpwned.picoxml.model.node.ref.CharRef;
import com.sigpwned.picoxml.model.node.ref.EntityRef;
import com.sigpwned.picoxml.util.XmlEncodings;
import com.sigpwned.picoxml.util.XmlStrings;

/**
 * Reads whole file into memory to build parse tree
 */
public class XmlReader {
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

  public static XMLParser defaultXMLParser(Reader r) throws IOException {
    CharStream s = CharStreams.fromReader(r);
    XMLLexer lexer = new XMLLexer(s);
    lexer.addErrorListener(new BaseErrorListener() {
      @Override
      public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
          boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        System.out.println("lexer.reportAmbiguity");
        // TODO Auto-generated method stub
        super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
      }

      @Override
      public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex,
          int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        System.out.println("lexer.reportAttemptingFullContext");
        // TODO Auto-generated method stub
        super.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts,
            configs);
      }

      @Override
      public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex,
          int stopIndex, int prediction, ATNConfigSet configs) {
        System.out.println("lexer.reportContextSensitivity");
        // TODO Auto-generated method stub
        super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs);
      }

      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
          int charPositionInLine, String msg, RecognitionException e) {
        System.out.println("lexer.syntaxError");
        e.printStackTrace(System.out);
        throw new InvalidSyntaxXmlException(line, charPositionInLine);
      }
    });
    TokenStream tokens = new CommonTokenStream(lexer);
    return new XMLParser(tokens);
  }

  private final XMLParser parser;

  public XmlReader(File f) throws IOException {
    this(f, StandardCharsets.UTF_8);
  }

  public XmlReader(File f, Charset defaultCharset) throws IOException {
    this(new FileInputStream(f), defaultCharset);
  }

  public XmlReader(InputStream in) throws IOException {
    this(in, StandardCharsets.UTF_8);
  }

  public XmlReader(InputStream in, Charset defaultCharset) throws IOException {
    this(XmlEncodings.decode(in, defaultCharset));
  }

  public XmlReader(String s) throws IOException {
    this(new StringReader(s));
  }

  public XmlReader(Reader r) throws IOException {
    this(defaultXMLParser(r));
  }

  protected XmlReader(XMLParser parser) {
    if (parser == null)
      throw new NullPointerException();
    this.parser = parser;
  }

  public Document document() {
    ANTLRErrorListener errorListener = new BaseErrorListener() {

      @Override
      public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
          boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        System.out.println("parser.reportAmbiguity");
        // TODO Auto-generated method stub
        super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
      }

      @Override
      public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex,
          int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        System.out.println("parser.reportAttemptingFullContext");
        // TODO Auto-generated method stub
        super.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts,
            configs);
      }

      @Override
      public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex,
          int stopIndex, int prediction, ATNConfigSet configs) {
        System.out.println("parser.reportContextSensitivity");
        // TODO Auto-generated method stub
        super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs);
      }

      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
          int charPositionInLine, String msg, RecognitionException e) {
        System.out.println("parser.syntaxError");
        System.out.println(msg);
        throw new InvalidSyntaxXmlException(line, charPositionInLine);
      }
    };
    getParser().addErrorListener(errorListener);
    getParser().addParseListener(new XMLParserBaseListener() {

      @Override
      public void visitTerminal(TerminalNode node) {
        // Token symbol = node.getSymbol();
        // switch (symbol.getType()) {
        // case XMLLexer.COMMENT:
        // // SYNTAX: <!--$CONTENT-->
        // if (symbol.getText().substring(4, symbol.getText().length() - 3).contains("--")) {
        // throw new InvalidSyntaxXmlException(symbol.getLine(), symbol.getCharPositionInLine());
        // }
        // break;
        // case XMLLexer.TEXT:
        // if (symbol.getText().contains("]]>")) {
        // // Per spec, characters cannot contain literal "]]>" sequence.
        // throw new InvalidSyntaxXmlException(symbol.getLine(), symbol.getCharPositionInLine());
        // }
        // break;
        // }
      }
    });

    try {
      return document(getParser().document());
    } finally {
      getParser().removeErrorListener(errorListener);
    }
  }

  private XMLParser getParser() {
    return parser;
  }

  protected Document document(DocumentContext c) {
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

  protected Misc misc(MiscContext c) {
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

  protected String elementName(ElementContext c) {
    return c.Name(0).getText();
  }

  protected Element element(ElementContext c) {
    // TODO For open tag/close tag, should we check the name on the close tag?

    Name name = Name.fromString(elementName(c));
    Attributes attributes = attributes(c.attribute());

    ElementNamespacing namespacing = addNamespaces(attributes);

    // TODO What if we can't resolve the namespace?
    String namespace = Optional.ofNullable(name.getPrefix()).flatMap(this::getPrefixNamespace)
        .orElse(getDefaultNamespace().orElse(null));

    for (Attribute attribute : attributes) {
      if (attribute.getPrefix() != null && !attribute.getPrefix().equals("xmlns")) {
        // TODO What if we can't resolve the namespace?
        attribute.setNamespace(getPrefixNamespace(attribute.getPrefix()).orElse(null));
      }
    }

    Nodes children;
    if (c.SLASH_CLOSE() != null) {
      // Self-closing tag
      children = Nodes.EMPTY;
    } else {
      // Open tag/close tag
      children = content(c.content());
    }

    removeNamespaces(namespacing);

    Element result = new Element(children, name.getPrefix(), name.getLocalName(), attributes);
    result.setNamespace(namespace);
    return result;
  }

  protected Nodes content(ContentContext c) {
    // Unfortunately, antlr4 does not track the order of rules this complex very neatly. To
    // reconstruct the order, we have to visit each subrule and sort them by the order they appear
    // in the document, which is available in getSourceInterval().
    List<Map.Entry<Integer, Node>> children = new ArrayList<>();

    // SYNTAX: <!CDATA[[$CONTENT]]>
    c.CDATA().stream()
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, cdata(n)))
        .forEach(children::add);

    // SYNTAX: SEA_WS | TEXT
    c.chardata().stream().map(n -> n.SEA_WS()).filter(Objects::nonNull)
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, whitespace(n)))
        .forEach(children::add);
    c.chardata().stream().map(n -> n.TEXT()).filter(Objects::nonNull)
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, text(n)))
        .forEach(children::add);

    // SYNTAX: <!--$CONTENT-->
    c.COMMENT().stream()
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, comment(n)))
        .forEach(children::add);

    // Hello, child elements!
    c.element().stream()
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, element(n)))
        .forEach(children::add);

    // SYNTAX: <?$NAME$CONTENT?>
    c.PI().stream().map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a,
        processingInstruction(n))).forEach(children::add);

    // SYNTAX: EntityRef | CharRef
    c.reference().stream().map(n -> n.EntityRef()).filter(Objects::nonNull)
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, entityRef(n)))
        .forEach(children::add);
    c.reference().stream().map(n -> n.CharRef()).filter(Objects::nonNull)
        .map(n -> new SimpleImmutableEntry<Integer, Node>(n.getSourceInterval().a, charRef(n)))
        .forEach(children::add);

    children.sort(Comparator.comparingInt(Map.Entry::getKey));

    return new Nodes(children.stream().map(Map.Entry::getValue).collect(toList()));
  }

  protected WhiteSpace whitespace(TerminalNode n) {
    return new WhiteSpace(n.getText());
  }

  protected Comment comment(TerminalNode n) {
    // SYNTAX: <!--$CONTENT-->
    return new Comment(n.getText().substring(4, n.getText().length() - 3));
  }

  protected CData cdata(TerminalNode n) {
    // SYNTAX: <!CDATA[[$CONTENT]]>
    return new CData(n.getText().substring(9, n.getText().length() - 3));
  }

  protected Text text(TerminalNode n) {
    return new Text(n.getText());
  }

  protected EntityRef entityRef(TerminalNode n) {
    // SYNTAX: &$NAME;
    return new EntityRef(n.getText().substring(1, n.getText().length() - 1));
  }

  protected CharRef charRef(TerminalNode n) {
    if (n.getText().startsWith("&#x")) {
      // SYNTAX: &#x$HEXDIGITS;
      return new CharRef(CharRef.HEX, n.getText().substring(3, n.getText().length() - 1));
    } else {
      // SYNTAX: &#$DIGITS;
      return new CharRef(CharRef.DEC, n.getText().substring(2, n.getText().length() - 1));
    }
  }

  protected ProcessingInstruction processingInstruction(TerminalNode n) {
    // SYNTAX: <?$NAME$CONTENT?>
    final String text = n.getText().substring(2, n.getText().length() - 2);
    int wsi = IntStream.range(0, text.length()).filter(i -> XmlStrings.whitespace(text.charAt(i)))
        .findFirst().orElse(text.length());
    final String pin = text.substring(0, wsi);
    final String pic = text.substring(wsi, text.length());
    return new ProcessingInstruction(pin, pic);
  }

  protected Prolog prolog(PrologContext c) {
    Attributes attributes = attributes(c.attribute());
    return new Prolog(new XmlDeclaration(attributes));
  }

  protected Attributes attributes(List<AttributeContext> cs) {
    return Attributes.of(cs.stream().map(this::attribute).collect(toList()));
  }

  protected Attribute attribute(AttributeContext c) {
    // TODO Should this have an EntityResolver?
    Name name = Name.fromString(c.Name().getText());
    String string = c.STRING().getText();
    String value = XmlStrings.unescape(string.substring(1, string.length() - 1));
    return new Attribute(name.getPrefix(), name.getLocalName(), value);
  }

  // NAMESPACE SCANNING ///////////////////////////////////////////////////////
  private ElementNamespacing addNamespaces(Attributes attributes) {
    boolean definesDefaultNamespace = false;
    Set<String> definesNamespacePrefixes = null;
    for (Attribute attribute : attributes) {
      if (attribute.getPrefix() == null && attribute.getLocalName().equals("xmlns")) {
        // TODO Validate namespace?
        pushDefaultNamespace(attribute.getValue());
        definesDefaultNamespace = true;
      } else if (Objects.equals(attribute.getPrefix(), "xmlns")) {
        // TODO Validate prefix?
        String namespacePrefix = attribute.getLocalName();
        // TODO Validate namespace?
        String namespace = attribute.getValue();
        // TODO See if we already have prefix? See if we already have namespace?
        pushPrefixNamespace(namespacePrefix, namespace);
        // handler.startPrefixMapping(namespacePrefix, namespace);
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

  // DEFAULT NAMESPACE ////////////////////////////////////////////////////////
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

  // NAMESPACE PREFIXES ///////////////////////////////////////////////////////
  private final Map<String, Stack<String>> namespacePrefixStacks = new HashMap<>();

  private Optional<String> getPrefixNamespace(String prefix) {
    return Optional.ofNullable(namespacePrefixStacks.get(prefix)).map(Stack::peek);
  }

  private void pushPrefixNamespace(String prefix, String namespace) {
    namespacePrefixStacks.computeIfAbsent(prefix, x -> new Stack<>()).push(namespace);
  }

  private void popPrefixNamespace(String prefix) {
    namespacePrefixStacks.compute(prefix, (x, xs) -> {
      xs.pop();
      return xs.isEmpty() ? null : xs;
    });
  }
}
