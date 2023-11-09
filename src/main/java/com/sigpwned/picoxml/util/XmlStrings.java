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
package com.sigpwned.picoxml.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class XmlStrings {
  private XmlStrings() {}

  public static boolean whitespace(char ch) {
    return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
  }

  /**
   * Escapes using standard entities only
   */
  public static String escape(String s) {
    final List<Map.Entry<String, Integer>> indexes = StandardEntities.values().stream()
        .map(v -> new SimpleEntry<String, Integer>(v, s.indexOf(v))).filter(e -> e.getValue() >= 0)
        .collect(toList());

    if (indexes.isEmpty())
      return s;

    int start = 0;
    final StringBuilder buf = new StringBuilder();
    while (!indexes.isEmpty()) {
      indexes.sort(Comparator.comparingInt(Map.Entry::getValue));

      final Map.Entry<String, Integer> next = indexes.remove(0);

      final int index = next.getValue();
      final String entityValue = next.getKey();
      final String entityName = StandardEntities.findStandardEntityName(entityValue)
          .orElseThrow(() -> new AssertionError("Failed to find standard entity " + entityValue));

      buf.append(s, start, index);
      buf.append("&").append(entityName).append(";");

      start = index + entityValue.length();

      final int nextIndex = s.indexOf(entityValue, start);
      if (nextIndex != -1) {
        next.setValue(nextIndex);
        indexes.add(next);
      }
    }

    buf.append(s, start, s.length());

    return buf.toString();
  }

  public static class UnescapedXmlString {
    private final boolean containsNakedAmpersand;
    private final List<String> unrecognizedEntities;
    private final String value;

    public UnescapedXmlString(boolean containsNakedAmpersand, List<String> unrecognizedEntities,
        String value) {
      if (unrecognizedEntities == null)
        throw new NullPointerException();
      if (value == null)
        throw new NullPointerException();
      this.containsNakedAmpersand = containsNakedAmpersand;
      this.unrecognizedEntities =
          unrecognizedEntities.isEmpty() ? emptyList() : unmodifiableList(unrecognizedEntities);
      this.value = value;
    }

    public boolean isContainsNakedAmpersand() {
      return containsNakedAmpersand;
    }

    public List<String> getUnrecognizedEntities() {
      return unrecognizedEntities;
    }

    public String getValue() {
      return value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(containsNakedAmpersand, unrecognizedEntities, value);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      UnescapedXmlString other = (UnescapedXmlString) obj;
      return containsNakedAmpersand == other.containsNakedAmpersand
          && Objects.equals(unrecognizedEntities, other.unrecognizedEntities)
          && Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
      return "UnescapedXmlString [containsNakedAmpersand=" + containsNakedAmpersand
          + ", unrecognizedEntities=" + unrecognizedEntities + ", value=" + value + "]";
    }
  }

  /**
   * Escapes using standard entities only
   */
  public static UnescapedXmlString unescape(String s) {
    int start = 0;
    int amp = s.indexOf("&");
    boolean containsNakedAmpersand = false;
    List<String> unrecognizedEntities = null;
    final StringBuilder buf = new StringBuilder();
    while (amp != -1) {
      buf.append(s, start, amp);

      int semi = s.indexOf(";", amp + 1);
      final String entityName;
      if (semi == -1) {
        // TODO Line and column
        containsNakedAmpersand = true;
        entityName = StandardEntities.AMP_NAME;
        semi = amp;
      } else {
        entityName = s.substring(amp + 1, semi);
      }
      final Optional<String> maybeEntityValue =
          StandardEntities.findStandardEntityValue(entityName);
      if (maybeEntityValue.isPresent()) {
        buf.append(maybeEntityValue.get());
      } else {
        // TODO Should we issue some kind of warning?
        if (unrecognizedEntities == null)
          unrecognizedEntities = new ArrayList<>();
        unrecognizedEntities.add(entityName);
        buf.append("&").append(entityName).append(";");
      }

      start = semi + 1;
      amp = s.indexOf("&", start);
    }

    buf.append(s, start, s.length());

    return new UnescapedXmlString(containsNakedAmpersand,
        Optional.ofNullable(unrecognizedEntities).orElseGet(Collections::emptyList),
        buf.toString());
  }
}
