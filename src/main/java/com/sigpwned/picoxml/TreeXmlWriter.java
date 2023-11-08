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
import java.io.Writer;
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
import com.sigpwned.picoxml.util.XmlStrings;

public class TreeXmlWriter {
  private final Writer writer;

  public TreeXmlWriter(Writer writer) {
    if (writer == null)
      throw new NullPointerException();
    this.writer = writer;
  }

  public void write(Document doc) throws IOException {
    if (doc.getProlog() != null)
      prolog(doc.getProlog());
    miscs(doc.getBeforeMiscs());
    element(doc.getRoot());
    miscs(doc.getAfterMiscs());
  }

  public void element(Element element) throws IOException {
    getWriter().write("<");
    if (element.getPrefix() != null) {
      getWriter().write(element.getPrefix());
      getWriter().write(":");
    }
    getWriter().write(element.getLocalName());
    attributes(element.getAttributes());

    if (element.getChildren().isEmpty()) {
      getWriter().write(" />");
    } else {
      getWriter().write(">");
      nodes(element.getChildren());
      getWriter().write("</");
      if (element.getPrefix() != null) {
        getWriter().write(element.getPrefix());
        getWriter().write(":");
      }
      getWriter().write(element.getLocalName());
      getWriter().write(">");
    }
  }

  public void miscs(Miscs miscs) throws IOException {
    for (Misc misc : miscs) {
      misc(misc);
    }
  }

  public void misc(Misc misc) throws IOException {
    if (misc instanceof Comment) {
      comment((Comment) misc);
    } else if (misc instanceof ProcessingInstruction) {
      processingInstruction((ProcessingInstruction) misc);
    } else if (misc instanceof WhiteSpace) {
      whiteSpace((WhiteSpace) misc);
    } else {
      throw new AssertionError(misc);
    }
  }

  public void nodes(Nodes nodes) throws IOException {
    for (Node node : nodes) {
      node(node);
    }
  }

  public void node(Node node) throws IOException {
    if (node instanceof CData) {
      cdata((CData) node);
    } else if (node instanceof Comment) {
      comment((Comment) node);
    } else if (node instanceof Element) {
      element((Element) node);
    } else if (node instanceof ProcessingInstruction) {
      processingInstruction((ProcessingInstruction) node);
    } else if (node instanceof Ref) {
      ref((Ref) node);
    } else if (node instanceof Text) {
      text((Text) node);
    } else {
      throw new AssertionError(node);
    }
  }

  public void cdata(CData cdata) throws IOException {
    getWriter().write("<!CDATA[[");
    getWriter().write(cdata.getContent());
    getWriter().write("]]>");

  }

  public void comment(Comment comment) throws IOException {
    getWriter().write("<!--");
    getWriter().write(comment.getContent());
    getWriter().write("-->");
  }

  public void processingInstruction(ProcessingInstruction processingInstruction)
      throws IOException {
    getWriter().write("<?");
    getWriter().write(processingInstruction.getName());
    getWriter().write(processingInstruction.getContent());
    getWriter().write("?>");
  }

  public void ref(Ref ref) throws IOException {
    if (ref instanceof EntityRef) {
      entityRef((EntityRef) ref);
    } else if (ref instanceof CharRef) {
      charRef((CharRef) ref);
    } else {
      throw new AssertionError(ref);
    }
  }

  public void entityRef(EntityRef entityRef) throws IOException {
    getWriter().write("&");
    getWriter().write(entityRef.getName());
    getWriter().write(";");
  }

  public void charRef(CharRef charRef) throws IOException {
    getWriter().write("&#");
    switch (charRef.getBase()) {
      case CharRef.DEC:
        // Nothing to do
        break;
      case CharRef.HEX:
        getWriter().write("x");
        break;
      default:
        throw new AssertionError(charRef.getBase());
    }
    getWriter().write(charRef.getDigits());
    getWriter().write(";");
  }

  public void text(Text text) throws IOException {
    getWriter().write(XmlStrings.escape(text.getContent()));
  }

  public void whiteSpace(WhiteSpace whiteSpace) throws IOException {
    getWriter().write(whiteSpace.getContent());
  }

  public void prolog(Prolog prolog) throws IOException {
    if (prolog.getDeclaration() != null)
      xmlDeclaration(prolog.getDeclaration());
  }

  public void xmlDeclaration(XmlDeclaration xmlDeclaration) throws IOException {
    getWriter().write("<?xml");
    attributes(xmlDeclaration.getAttributes());
    getWriter().write(" ");
    getWriter().write("?>");
  }

  public void attributes(Attributes attributes) throws IOException {
    for (Attribute attribute : attributes) {
      getWriter().write(" ");
      attribute(attribute);
    }
  }

  public void attribute(Attribute attribute) throws IOException {
    if (attribute.getPrefix() != null) {
      getWriter().write(attribute.getPrefix());
      getWriter().write(":");
    }
    getWriter().write(attribute.getLocalName());
    getWriter().write("=");
    getWriter().write("\"");
    getWriter().write(XmlStrings.escape(attribute.getValue()));
    getWriter().write("\"");
  }

  private Writer getWriter() {
    return writer;
  }
}
