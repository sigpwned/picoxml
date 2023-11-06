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
package com.sigpwned.picoxml.util;

import static java.util.stream.Collectors.toList;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class XmlStrings {
  private XmlStrings() {}

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

  /**
   * Escapes using standard entities only
   */
  public static String unescape(String s) {
    int start = 0;
    int amp = s.indexOf("&");
    final StringBuilder buf = new StringBuilder();
    while (amp != -1) {
      buf.append(s, start, amp);

      final int semi = s.indexOf(";", amp + 1);
      final String entityName = s.substring(amp + 1, semi);
      final Optional<String> maybeEntityValue =
          StandardEntities.findStandardEntityValue(entityName);
      if (maybeEntityValue.isPresent()) {
        buf.append(maybeEntityValue.get());
      } else {
        buf.append("&").append(entityName).append(";");
      }

      start = semi + 1;
      amp = s.indexOf("&", start);
    }

    buf.append(s, start, s.length());

    return buf.toString();
  }
}
