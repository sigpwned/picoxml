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
package com.sigpwned.picoxml.model;

import java.util.Objects;

public class Attribute {
  public static Attribute of(String prefix, String localName, String value) {
    return new Attribute(prefix, localName, value);
  }

  private final String prefix;
  private final String localName;
  private final String value;
  private String namespace;

  public Attribute(String prefix, String localName, String value) {
    if (localName == null)
      throw new NullPointerException();
    this.prefix = prefix;
    this.localName = localName;
    this.value = value;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getLocalName() {
    return localName;
  }

  public String getValue() {
    return value;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Override
  public int hashCode() {
    return Objects.hash(localName, namespace, prefix, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Attribute other = (Attribute) obj;
    return Objects.equals(localName, other.localName) && Objects.equals(namespace, other.namespace)
        && Objects.equals(prefix, other.prefix) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "Attribute [prefix=" + prefix + ", localName=" + localName + ", value=" + value
        + ", namespace=" + namespace + "]";
  }
}
