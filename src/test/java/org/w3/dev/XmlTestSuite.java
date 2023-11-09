package org.w3.dev;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import com.sigpwned.picoxml.XmlReader;
import com.sigpwned.picoxml.exception.InvalidSyntaxXmlException;
import com.sigpwned.picoxml.model.Attribute;
import com.sigpwned.picoxml.model.node.Element;

public class XmlTestSuite {
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

  public static List<XmlTest> xmltests;

  @BeforeClass
  public static void setupXmlTestSuiteClass() throws IOException {
    try (InputStream in = XmlTestSuite.class.getResourceAsStream("xmltest.xml")) {
      xmltests = new XmlReader(in).document().getRoot().getChildren().stream()
          .filter(child -> child instanceof Element).map(child -> (Element) child)
          .filter(element -> element.getLocalName().equals("TEST")).map(XmlTest::fromXml)
          .collect(toList());
    }
  }

  /**
   * We should have exactly 366 tests
   */
  @Test
  public void suiteTest() {
    assertThat(xmltests.size(), is(366));
  }

  @Test
  public void notWellFormedTests() throws IOException {
    List<XmlTest> nwftests =
        xmltests.stream().filter(t -> t.getType().equals("not-wf")).collect(toList());

    // There should be exactly 196 non-well formed tests
    assertThat(nwftests.size(), is(196));

    for (XmlTest test : nwftests) {
      try (InputStream in = XmlTestSuite.class.getResourceAsStream(test.getInputUri())) {
        Exception problem = null;
        try {
          System.out.println("before " + test.getId());
          new XmlReader(in).document();
          System.out.println("oops");
        } catch (InvalidSyntaxXmlException e) {
          problem = e;
        }
        assertThat(problem, notNullValue());
      }
    }
  }
}
