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
package com.sigpwned.picoxml.model.node;

import java.util.Objects;
import com.sigpwned.picoxml.model.Attributes;
import com.sigpwned.picoxml.model.Node;
import com.sigpwned.picoxml.model.Nodes;

public class Element extends Node {
  private final String prefix;
  private final String localName;
  private final Attributes attributes;
  private String namespace;

  public Element(Nodes children, String prefix, String localName, Attributes attributes) {
    super(children);
    if (localName == null)
      throw new NullPointerException();
    if (attributes == null)
      throw new NullPointerException();
    this.prefix = prefix;
    this.localName = localName;
    this.attributes = attributes;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getLocalName() {
    return localName;
  }

  public Attributes getAttributes() {
    return attributes;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(attributes, localName, namespace, prefix);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Element other = (Element) obj;
    return Objects.equals(attributes, other.attributes)
        && Objects.equals(localName, other.localName) && Objects.equals(namespace, other.namespace)
        && Objects.equals(prefix, other.prefix);
  }

  @Override
  public String toString() {
    return "Element [prefix=" + prefix + ", localName=" + localName + ", attributes=" + attributes
        + ", namespace=" + namespace + "]";
  }
}
