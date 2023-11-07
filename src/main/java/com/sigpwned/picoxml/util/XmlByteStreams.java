package com.sigpwned.picoxml.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class XmlByteStreams {
  private XmlByteStreams() {}

  public static Reader decode(InputStream in) throws IOException {
    return decode(in, StandardCharsets.UTF_8);
  }

  public static Reader decode(InputStream in, Charset defaultCharset) throws IOException {
    // TODO Check BOMs
    // TODO Read <?xml tag
    return new InputStreamReader(in, defaultCharset);
  }
}
