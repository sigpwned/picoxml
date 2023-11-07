package com.sigpwned.picoxml;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import com.sigpwned.picoxml.util.XmlStrings;

public class StreamingXmlWriter {
  // TODO Fix bug where user could close document and re-open it
  // TODO Fix bug where user could close root element and open a new one

  /**
   * The virtual, topmost document tag. Not materialized. Purely for application logic.
   */
  private static final int TYPE_DOCUMENT = 1;

  /**
   * A self-closing tag, e.g., <tag />
   */
  private static final int TYPE_SELF_CLOSE = 2;

  /**
   * A traditional open/close tag, e.g., <tag></tag>
   */
  private static final int TYPE_OPEN_CLOSE = 3;

  private static class ElementState {
    public ElementState(String prefix, String localName, int type) {
      this.prefix = prefix;
      this.localName = localName;
      this.type = type;
      this.open = true;
    }

    private final String prefix;
    private final String localName;
    private final int type;
    private boolean open;
    private String defaultNamespace;
    private Map<String, String> prefixNamespaces;
    private Map<String, String> namespacePrefixes;

    public String getPrefix() {
      return prefix;
    }

    public String getLocalName() {
      return localName;
    }

    public int getType() {
      return type;
    }

    public boolean getOpen() {
      return open;
    }

    public void setOpen(boolean open) {
      this.open = open;
    }

    public String getDefaultNamespace() {
      return defaultNamespace;
    }

    public void setDefaultNamespace(String newDefaultNamespace) {
      if (newDefaultNamespace == null)
        throw new NullPointerException();
      if (defaultNamespace != null)
        throw new IllegalStateException("default namespace already set for this tag");
      this.defaultNamespace = newDefaultNamespace;
    }

    public void addNamespace(String prefix, String namespace) {
      if (prefix == null)
        throw new NullPointerException();
      if (namespace == null)
        throw new NullPointerException();
      if (prefix.equals(DEFAULT_NAMESPACE))
        throw new IllegalArgumentException("use setDefaultNamespace() to set default namespace");
      if (prefixNamespaces != null && prefixNamespaces.containsKey(prefix))
        throw new IllegalStateException("prefix " + prefix + " already set for this tag");
      if (namespacePrefixes != null && namespacePrefixes.containsKey(namespace))
        throw new IllegalStateException("namespace " + namespace + " already set for this tag");

      if (prefixNamespaces == null)
        prefixNamespaces = new HashMap<>(4);
      if (namespacePrefixes == null)
        namespacePrefixes = new HashMap<>(4);

      prefixNamespaces.put(prefix, namespace);
      namespacePrefixes.put(namespace, prefix);
    }

    public String getNamespacePrefix(String namespace) {
      return namespacePrefixes.get(namespace);
    }

    public String getPrefixNamespace(String prefix) {
      return prefixNamespaces.get(prefix);
    }
  }

  private static final String DEFAULT_NAMESPACE = "";

  private final Writer writer;

  public StreamingXmlWriter(Writer writer) {
    if (writer == null)
      throw new NullPointerException();
    this.writer = writer;
  }

  public void writeEntityRef(String name) {
    requireContentWriteable();
    write("&");
    write(name);
    write(";");
  }

  public void writeCData(String data) {
    requireContentWriteable();
    write("<!CDATA[[");
    write(data);
    write("]]>");
  }

  public void writeCharacters(char[] text, int start, int len) {
    requireContentWriteable();
    write(text, start, len);
  }

  public void writeCharacters(String text) {
    requireContentWriteable();
    write(text);
  }

  public void writeComment(String data) {
    requireContentWriteable();
    write("<!--");
    write(data);
    write("-->");
  }

  public void writeStartElement(String localName) {
    writeStartElement(DEFAULT_NAMESPACE, localName, DEFAULT_NAMESPACE);
  }

  public void writeStartElement(String namespaceURI, String localName) {
    String prefix = findPrefixByNamespace(namespaceURI)
        .orElseThrow(() -> new IllegalArgumentException("no namespace " + namespaceURI));
    writeStartElement(prefix, localName, namespaceURI);
  }

  public void writeStartElement(String prefix, String localName, String namespaceURI) {
    requireElementWriteable();
    write("<");
    if (!prefix.equals(DEFAULT_NAMESPACE)) {
      write(prefix);
      write(":");
    }
    write(localName);
    pushState(new ElementState(prefix, localName, TYPE_OPEN_CLOSE));
  }

  public void writeEndElement() {
    requireElementWriteable();
    ElementState state = popState(TYPE_OPEN_CLOSE);
    if (state.getType() == TYPE_DOCUMENT) {
      pushState(state);
      throw new IllegalStateException("no open elements");
    }

    write("</");
    if (!state.getPrefix().equals(DEFAULT_NAMESPACE)) {
      write(state.getPrefix());
      write(":");
    }
    write(state.getLocalName());
    write(">");
  }

  public void writeEmptyElement(String localName) {
    writeEmptyElement(DEFAULT_NAMESPACE, localName, DEFAULT_NAMESPACE);
  }

  public void writeEmptyElement(String namespaceURI, String localName) {
    String prefix = findPrefixByNamespace(namespaceURI)
        .orElseThrow(() -> new IllegalArgumentException("no namespace " + namespaceURI));
    writeEmptyElement(prefix, localName, namespaceURI);
  }

  public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
    requireElementWriteable();
    write("<");
    write(prefix);
    write(":");
    write(localName);
    pushState(new ElementState(prefix, localName, TYPE_SELF_CLOSE));
  }

  public void writeDefaultNamespace(String namespaceURI) {
    writeAttribute("xmlns", namespaceURI);
    peekState().setDefaultNamespace(namespaceURI);
  }

  public void writeNamespace(String prefix, String namespace) {
    writeAttribute("xmlns", namespace, prefix, namespace);
    peekState().addNamespace(prefix, namespace);
  }

  public void writeAttribute(String localName, String value) {
    writeAttribute(DEFAULT_NAMESPACE, DEFAULT_NAMESPACE, localName, value);
  }

  public void writeAttribute(String namespaceURI, String localName, String value) {
    String prefix = findPrefixByNamespace(namespaceURI)
        .orElseThrow(() -> new IllegalArgumentException("no namespace " + namespaceURI));
    writeAttribute(prefix, namespaceURI, localName, value);
  }

  public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
    requireAttributeWriteable();
    write(" ");
    if (!prefix.equals(DEFAULT_NAMESPACE)) {
      write(prefix);
      write(":");
    }
    write(localName);
    write("=\"");
    write(XmlStrings.escape(value));
    write("\"");
  }

  public void writeProcessingInstruction(String target) {
    writeProcessingInstruction(target, "");
  }

  public void writeProcessingInstruction(String target, String data) {
    requireElementWriteable();
    write("<?");
    write(target);
    write(" ");
    write(data);
    write("?>");
  }

  public void writeStartDocument() {
    writeStartDocument("1.0");
  }

  public void writeStartDocument(String version) {
    writeStartDocument(null, version);
  }

  public void writeStartDocument(String encoding, String version) {
    if (peekState() != null)
      throw new IllegalStateException("document already open");
    write("<?xml");
    if (version != null) {
      write(" version=\"");
      write(XmlStrings.escape(version));
      write("\"");
    }
    if (encoding != null) {
      write(" encoding=\"");
      write(XmlStrings.escape(encoding));
      write("\"");
    }
    write(" ?>");
    pushState(new ElementState("", "", TYPE_DOCUMENT));
  }

  public void writeEndDocument() {
    requireElementWriteable();
    @SuppressWarnings("unused")
    ElementState state = popState(TYPE_DOCUMENT);
  }

  // ELEMENT OPEN OR CLOSED ///////////////////////////////////////////////////
  private void requireElementWriteable() {
    ElementState state = peekState();
    if (state == null) {
      // If there are no elements, then we haven't opened the document.
      throw new IllegalStateException("document not open");
    }

    if (state.getOpen() == false) {
      // The innermost open element tag is closed. Life is good.
      return;
    }

    // The innermost open element tag is still open. Close it.
    writeTagClosing();
  }

  private void requireContentWriteable() {
    ElementState state = peekState();
    if (state == null) {
      // If there are no elements, then we haven't opened the document.
      throw new IllegalStateException("document not open");
    }

    if (state.getType() == TYPE_DOCUMENT) {
      // We have opened the document, but not opened an element.
      throw new IllegalStateException("no elements");
    }

    if (state.getOpen() == false) {
      // The innermost open element tag is closed. Life is good.
      return;
    }

    // The innermost open element tag is still open. Close it.
    writeTagClosing();
  }

  private void requireAttributeWriteable() {
    ElementState state = peekState();
    if (state == null) {
      // If there are no elements, then we haven't opened the document.
      throw new IllegalStateException("document not open");
    }

    if (state.getType() == TYPE_DOCUMENT) {
      // We have opened the document, but not opened an element.
      throw new IllegalStateException("no elements");
    }

    if (state.getOpen() == false) {
      // The innermost element tag is closed, so not ready for attributes.
      throw new IllegalStateException("innermost element is closed");
    }

    // The innermost element tag is open, so ready for attributes.
  }

  private void writeTagClosing() {
    ElementState state = peekState();
    if (state == null)
      throw new IllegalStateException("no elements");
    if (state.getOpen() == false)
      throw new IllegalStateException("innermost element not open");
    switch (state.getType()) {
      case TYPE_DOCUMENT:
        throw new AssertionError("cannot close virtual document tag");
      case TYPE_SELF_CLOSE:
        write(" />");
        break;
      case TYPE_OPEN_CLOSE:
        write(">");
        break;
      default:
        throw new AssertionError(state.type);
    }
    state.setOpen(false);
  }

  // I/O //////////////////////////////////////////////////////////////////////

  private void write(String text) {
    try {
      writer.write(text);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void write(char[] chbuf, int off, int len) {
    try {
      writer.write(chbuf, off, len);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void close() {
    try {
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void flush() {
    try {
      writer.flush();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  // NAMESPACES AND PREFIXES //////////////////////////////////////////////////
  @SuppressWarnings("unused")
  private Optional<String> findDefaultNamespace() {
    ListIterator<ElementState> iterator = states.listIterator(states.size());
    while (iterator.hasPrevious()) {
      ElementState state = iterator.previous();
      if (state.defaultNamespace != null)
        return Optional.of(state.defaultNamespace);
    }
    return Optional.empty();
  }

  private Optional<String> findPrefixByNamespace(String namespace) {
    boolean defaulted = false;
    ListIterator<ElementState> iterator = states.listIterator(states.size());
    while (iterator.hasPrevious()) {
      ElementState state = iterator.previous();
      if (state.getDefaultNamespace() != null && !defaulted) {
        if (state.getDefaultNamespace().equals(namespace))
          return Optional.of(DEFAULT_NAMESPACE);
        defaulted = true;
      }
      String prefix = state.getNamespacePrefix(namespace);
      if (prefix != null)
        return Optional.of(prefix);
    }
    return Optional.empty();
  }

  @SuppressWarnings("unused")
  private Optional<String> findNamespaceByPrefix(String prefix) {
    ListIterator<ElementState> iterator = states.listIterator(states.size());
    while (iterator.hasPrevious()) {
      ElementState state = iterator.previous();
      if (state.getDefaultNamespace() != null) {
        if (prefix.equals(""))
          return Optional.of(state.getDefaultNamespace());
      }
      String namespace = state.getPrefixNamespace(prefix);
      if (namespace != null)
        return Optional.of(namespace);
    }
    return Optional.empty();
  }

  // ELEMENT STATE ////////////////////////////////////////////////////////////

  private final Stack<ElementState> states = new Stack<>();

  private ElementState peekState() {
    return states.isEmpty() ? null : states.peek();
  }

  private ElementState pushState(ElementState state) {
    states.push(state);
    return state;
  }

  private ElementState popState(int type) {
    // This will be a race condition if we go concurrent, hence the check
    ElementState peek = peekState();
    if (peek == null)
      throw new IllegalStateException("no elements open");
    if (peek.getType() != type)
      throw new IllegalStateException("unexpected element type");
    ElementState result = states.pop();
    if (result != peek) {
      // This should never happen under normal single-threaded usage
      throw new ConcurrentModificationException();
    }
    return result;
  }
}
