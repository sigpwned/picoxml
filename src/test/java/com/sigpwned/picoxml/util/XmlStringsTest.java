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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class XmlStringsTest {
  @Test
  public void escapeTest() {
    assertThat(
        XmlStrings
            .escape("alpha \" bravo ' charlie & delta < echo > foxtrot \" golf \" hotel \" india"),
        is("alpha &quot; bravo &apos; charlie &amp; delta &lt; echo &gt; foxtrot &quot; golf &quot; hotel &quot; india"));
  }

  @Test
  public void unescapeTest() {
    assertThat(XmlStrings.unescape(
        "alpha &quot; bravo &apos; charlie &amp; delta &lt; echo &gt; foxtrot &quot; golf &quot; hotel &quot; india")
        .getValue(),
        is("alpha \" bravo ' charlie & delta < echo > foxtrot \" golf \" hotel \" india"));
  }
}
