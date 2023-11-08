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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;

public class StreamingXmlWriterTest {
  /*
   * <?xml version="1.0" encoding="UTF-8"?> <?foobar This is a processing instruction. ?> <greeting
   * alpha="bravo" test="1 &lt; 2"> <hello>world</hello> <!-- This is a comment --> <entities>&lt;
   * &gt; &apos; &quot; &#65; &#x41;</entities> <selfclosed name='value' /> <![CDATA[ This is CDATA.
   * ]]> This is chardata. </greeting>
   */
  @Test
  public void test() throws IOException {
    StringWriter buf = new StringWriter();
    try {
      StreamingXmlWriter w = new StreamingXmlWriter(buf);
      w.writeStartDocument("UTF-8", "1.0");
      w.writeProcessingInstruction("foobar", "This is a processing instruction.");
      w.writeStartElement("greeting");
      w.writeAttribute("alpha", "bravo");
      w.writeAttribute("test", "1 < 2");
      w.writeStartElement("hello");
      w.writeCharacters("world");
      w.writeEndElement();
      w.writeComment("This is a comment");
      w.writeStartElement("entities");
      w.writeCharacters("< > ' \" A A");
      w.writeEndElement();
      w.writeStartElement("selfclosed");
      w.writeAttribute("name", "value");
      w.writeEndElement();
      w.writeCData("This is CDATA.");
      w.writeCharacters("This is chardata.");
      w.writeEndElement();
      w.writeEndDocument();
    } finally {
      buf.close();
    }
    assertThat(buf.toString(), is(
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><?foobar This is a processing instruction.?><greeting alpha=\"bravo\" test=\"1 &lt; 2\"><hello>world</hello><!--This is a comment--><entities>&lt; &gt; &apos; &quot; A A</entities><selfclosed name=\"value\"></selfclosed><!CDATA[[This is CDATA.]]>This is chardata.</greeting>"));
  }
}
