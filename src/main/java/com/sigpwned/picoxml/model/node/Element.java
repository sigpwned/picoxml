/*-
 * =================================LICENSE_START==================================
 * picoxml
 * ====================================SECTION=====================================
 * Copyright (C) 2023 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        + ", namespace=" + namespace + ", children=" + getChildren() + "]";
  }
}
