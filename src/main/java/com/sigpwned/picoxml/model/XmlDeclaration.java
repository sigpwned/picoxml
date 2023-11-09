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

public class XmlDeclaration {
  private final Attributes attributes;

  public XmlDeclaration(Attributes attributes) {
    if (attributes == null)
      throw new NullPointerException();
    this.attributes = attributes;
  }

  public Attributes getAttributes() {
    return attributes;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributes);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    XmlDeclaration other = (XmlDeclaration) obj;
    return Objects.equals(attributes, other.attributes);
  }

  @Override
  public String toString() {
    return "XmlDeclaration [attributes=" + attributes + "]";
  }
}
