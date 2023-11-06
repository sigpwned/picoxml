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

import static java.util.Collections.unmodifiableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class StandardEntities {
  private StandardEntities() {}

  public static final String AMP_NAME = "amp";

  public static final String AMP_VALUE = "&";

  public static final String LT_NAME = "lt";

  public static final String LT_VALUE = "<";

  public static final String GT_NAME = "gt";

  public static final String GT_VALUE = ">";

  public static final String QUOT_NAME = "quot";

  public static final String QUOT_VALUE = "\"";

  public static final String APOS_NAME = "apos";

  public static final String APOS_VALUE = "\'";

  public static final Map<String, String> NAME_TO_VALUE;
  static {
    Map<String, String> m = new HashMap<>(5);
    m.put(AMP_NAME, AMP_VALUE);
    m.put(APOS_NAME, APOS_VALUE);
    m.put(GT_NAME, GT_VALUE);
    m.put(LT_NAME, LT_VALUE);
    m.put(QUOT_NAME, QUOT_VALUE);
    NAME_TO_VALUE = unmodifiableMap(m);
  }

  public static final Map<String, String> VALUE_TO_NAME;
  static {
    Map<String, String> m = new HashMap<>(5);
    m.put(AMP_VALUE, AMP_NAME);
    m.put(APOS_VALUE, APOS_NAME);
    m.put(GT_VALUE, GT_NAME);
    m.put(LT_VALUE, LT_NAME);
    m.put(QUOT_VALUE, QUOT_NAME);
    VALUE_TO_NAME = unmodifiableMap(m);
  }

  public static Set<String> names() {
    return NAME_TO_VALUE.keySet();
  }

  public static Set<String> values() {
    return VALUE_TO_NAME.keySet();
  }

  public static Optional<String> findStandardEntityValue(String name) {
    return Optional.ofNullable(NAME_TO_VALUE.get(name));
  }

  public static Optional<String> findStandardEntityName(String value) {
    return Optional.ofNullable(VALUE_TO_NAME.get(value));
  }
}
