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
