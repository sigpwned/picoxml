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
package org.w3.dev;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.sigpwned.picoxml.XmlReader;
import com.sigpwned.picoxml.exception.InvalidSyntaxXmlException;
import com.sigpwned.picoxml.model.Attribute;
import com.sigpwned.picoxml.model.node.Element;

public class XmlSuiteTests {
  public static class XmlTest {
    public static XmlTest fromXml(Element element) {
      String type =
          element.getAttributes().findAttributeByLocalName("TYPE").map(Attribute::getValue).get();
      String entities = element.getAttributes().findAttributeByLocalName("ENTITIES")
          .map(Attribute::getValue).get();
      String id =
          element.getAttributes().findAttributeByLocalName("ID").map(Attribute::getValue).get();
      String inputUri =
          element.getAttributes().findAttributeByLocalName("URI").map(Attribute::getValue).get();
      String outputUri = element.getAttributes().findAttributeByLocalName("OUTPUT")
          .map(Attribute::getValue).orElse(null);
      return of(type, entities, id, inputUri, outputUri);
    }

    public static XmlTest of(String type, String entities, String id, String inputUri,
        String outputUri) {
      return new XmlTest(type, entities, id, inputUri, outputUri);
    }

    private final String type;
    private final String entities;
    private final String id;
    private final String inputUri;
    private final String outputUri;

    public XmlTest(String type, String entities, String id, String inputUri, String outputUri) {
      this.type = requireNonNull(type);
      this.entities = requireNonNull(entities);
      this.id = requireNonNull(id);
      this.inputUri = requireNonNull(inputUri);
      this.outputUri = outputUri;
    }

    public String getType() {
      return type;
    }

    public String getEntities() {
      return entities;
    }

    public String getId() {
      return id;
    }

    public String getInputUri() {
      return inputUri;
    }

    public String getOutputUri() {
      return outputUri;
    }
  }

  private static class TestPerformance {
    public static TestPerformance of(int passed, int failed) {
      return new TestPerformance(passed, failed);
    }

    private final int passed;
    private final int failed;

    public TestPerformance(int passed, int failed) {
      this.passed = passed;
      this.failed = failed;
    }

    public int getPassed() {
      return passed;
    }

    public int getFailed() {
      return failed;
    }

    @Override
    public int hashCode() {
      return Objects.hash(failed, passed);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TestPerformance other = (TestPerformance) obj;
      return failed == other.failed && passed == other.passed;
    }

    @Override
    public String toString() {
      return "TestPerformance [passed=" + passed + ", failed=" + failed + "]";
    }
  }

  public static List<XmlTest> xmltests;

  @BeforeClass
  public static void setupXmlTestSuiteClass() throws IOException {
    try (InputStream in = XmlSuiteTests.class.getResourceAsStream("xmltest.xml")) {
      xmltests = new XmlReader(in).document().getRoot().getChildren().stream()
          .filter(child -> child instanceof Element).map(child -> (Element) child)
          .filter(element -> element.getLocalName().equals("TEST")).map(XmlTest::fromXml)
          .collect(toList());
    }
  }

  /**
   * Make sure our tests loaded properly. We should have exactly 366 tests
   */
  @Test
  public void suiteTest() {
    assertThat(xmltests.size(), is(366));
  }

  /**
   * As a permissive, non-validating parser, we simply will not pick up all non-well formed
   * documents, so ignore these tests.
   */
  @Test
  @Ignore
  public void notWellFormedTests() throws IOException {
    TestPerformance performance = runTestSet(t -> t.getType().equals("not-wf"));
    assertThat(performance, is(TestPerformance.of(0, 196)));
  }

  /**
   * As a permissive, non-validating parser, we simply will not pick up all invalid documents, so
   * ignore these tests.
   */
  @Test
  @Ignore
  public void invalidTests() throws IOException {
    TestPerformance performance = runTestSet(t -> t.getType().equals("invalid"));
    assertThat(performance, is(TestPerformance.of(0, 7)));
  }

  /**
   * All of these tests should pass.
   */
  @Test
  public void validTests() throws IOException {
    TestPerformance performance = runTestSet(t -> t.getType().equals("valid"));
    assertThat(performance, is(TestPerformance.of(163, 0)));
  }

  private TestPerformance runTestSet(Predicate<XmlTest> selector) throws IOException {
    List<XmlTest> tests = xmltests.stream().filter(selector).collect(toList());

    // Try all our tests
    int passed = 0, failed = 0;
    for (XmlTest test : tests) {
      try (InputStream in = XmlSuiteTests.class.getResourceAsStream(test.getInputUri())) {
        Exception problem = null;
        try {
          new XmlReader(in).document();
        } catch (InvalidSyntaxXmlException e) {
          problem = e;
        }
        if (problem != null) {
          System.out.println("Failed test " + test.id);
          failed = failed + 1;
        } else {
          passed = passed + 1;
        }
      }
    }

    return TestPerformance.of(passed, failed);
  }
}
