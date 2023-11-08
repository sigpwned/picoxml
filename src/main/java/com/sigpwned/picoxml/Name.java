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

import java.util.Objects;

public class Name {
  public static Name of(String prefix, String localName) {
    return new Name(prefix, localName);
  }

  public static Name fromString(String name) {
    final int colon = name.indexOf(":");
    if (colon != -1) {
      // So this is clearly a qname
      final String qname = name;
      // TODO What if prefix is empty?
      final String prefix = qname.substring(0, colon);
      // TODO What if name is empty?
      final String localName = qname.substring(colon + 1, qname.length());
      return new Name(prefix, localName);
    } else {
      return new Name(null, name);
    }
  }

  private final String prefix;
  private final String localName;

  public Name(String prefix, String localName) {
    if (localName == null)
      throw new NullPointerException();
    this.prefix = prefix;
    this.localName = localName;
  }

  public String getPrefix() {
    return prefix;
  }

  public boolean isQualified() {
    return getPrefix() != null;
  }

  public String getLocalName() {
    return localName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(localName, prefix);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Name other = (Name) obj;
    return Objects.equals(localName, other.localName) && Objects.equals(prefix, other.prefix);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (getPrefix() != null) {
      result.append(getPrefix());
      result.append(":");
    }
    result.append(getLocalName());
    return result.toString();
  }
}
