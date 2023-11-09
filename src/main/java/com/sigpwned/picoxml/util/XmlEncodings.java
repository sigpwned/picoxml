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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

public final class XmlEncodings {
  private XmlEncodings() {}

  public static final byte[] UTF_8_BOM = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

  public static final byte[] UTF_16_BE_BOM = new byte[] {(byte) 0xFE, (byte) 0xFF};

  public static final byte[] UTF_16_LE_BOM = new byte[] {(byte) 0xFF, (byte) 0xFE};

  private static final int MAX_BOM_LEN =
      IntStream.of(UTF_8_BOM.length, UTF_16_BE_BOM.length, UTF_16_LE_BOM.length).max().getAsInt();

  private static final int MIN_LOOKAHEAD_LEN = MAX_BOM_LEN;

  public static Reader decode(InputStream in, Charset defaultCharset) throws IOException {
    return decode(new PushbackInputStream(in, MIN_LOOKAHEAD_LEN), defaultCharset);
  }

  public static Reader decode(PushbackInputStream in, Charset defaultCharset) throws IOException {
    Charset encoding = detectEncoding(in, defaultCharset);
    return new InputStreamReader(in, encoding);
  }

  public static Charset detectEncoding(PushbackInputStream in, Charset defaultCharset)
      throws IOException {
    // Our window needs to include any BOM, or at least 2 bytes
    byte[] lookahead = peek(in, MIN_LOOKAHEAD_LEN);

    // Check our BOMs
    if (startsWith(lookahead, UTF_8_BOM)) {
      readFully(in, lookahead, 0, UTF_8_BOM.length);
      return StandardCharsets.UTF_8;
    }
    if (startsWith(lookahead, UTF_16_BE_BOM)) {
      readFully(in, lookahead, 0, UTF_16_BE_BOM.length);
      return StandardCharsets.UTF_16BE;
    }
    if (startsWith(lookahead, UTF_16_LE_BOM)) {
      readFully(in, lookahead, 0, UTF_16_LE_BOM.length);
      return StandardCharsets.UTF_16LE;
    }

    // Check for multibyte characters
    if (lookahead[0] == '<' && lookahead[1] == '\0') {
      return StandardCharsets.UTF_16LE;
    }
    if (lookahead[0] == '\0' && lookahead[1] == '<') {
      return StandardCharsets.UTF_16BE;
    }

    // TODO Read <?xml tag
    // This is a single-byte charset. Just assume UTF-8. That will cover ASCII, but will return
    // weirdness for US-LATIN-1/ISO-8859-1.
    return StandardCharsets.UTF_8;
  }

  private static byte[] peek(PushbackInputStream in, int len) throws IOException {
    byte[] result = new byte[len];
    readFully(in, result);
    in.unread(result);
    return result;
  }

  private static void readFully(InputStream in, byte[] buf) throws IOException {
    readFully(in, buf, 0, buf.length);
  }

  private static void readFully(InputStream in, byte[] buf, int off, int len) throws IOException {
    int start = off, end = off + len;
    while (start < end) {
      int nread = in.read(buf, start, end - start);
      if (nread == -1)
        throw new EOFException();
      start = start + nread;
    }
  }

  private static boolean startsWith(byte[] haystack, byte[] needle) {
    if (needle.length > haystack.length)
      return false;
    int len = needle.length;
    for (int i = 0; i < len; i++)
      if (haystack[i] != needle[i])
        return false;
    return true;
  }
}
