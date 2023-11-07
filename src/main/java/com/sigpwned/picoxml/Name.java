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
