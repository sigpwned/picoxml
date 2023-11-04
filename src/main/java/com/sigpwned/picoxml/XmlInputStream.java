package com.sigpwned.picoxml;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntPredicate;

/**
 * XML parsed entities are often stored in computer files which, for editing convenience, are
 * organized into lines. These lines are typically separated by some combination of the characters
 * CARRIAGE RETURN (#xD) and LINE FEED (#xA).
 *
 * To simplify the tasks of applications, the XML processor must behave as if it normalized all line
 * breaks in external parsed entities (including the document entity) on input, before parsing, by
 * translating both the two-character sequence #xD #xA and any #xD that is not followed by #xA to a
 * single #xA character.
 *
 * https://www.w3.org/TR/REC-xml/#sec-line-ends
 */
public class XmlInputStream {
  public int read() {}

  public void unread(int cp) {}

  public void unread(String cps) {}

  public int peek() {

  }

  public int peek(int count) {

  }

  public boolean lookahead(int cp) {}

  public boolean lookahead(String cps) {}

  public boolean attempt(int cp) {}

  public boolean attempt(String cps) {

  }

  public boolean attempt(StringBuilder buf, int cp) {}

  public OptionalInt attempt(StringBuilder buf, int[] cps) {}

  public boolean attempt(StringBuilder buf, String cps) {}

  public Optional<String> attempt(StringBuilder buf, String[] cpss) {}

  public boolean attempt(StringBuilder buf, IntPredicate p) {}

  public void expect(StringBuilder buf, int cp) {}

  /**
   * Expect one of the given code points, returning the code point selected. Otherwise, throw an
   * exception.
   */
  public int expect(StringBuilder buf, int[] cps) {}

  public void expect(StringBuilder buf, String cps) {

  }

  /**
   * Expect one of the given strings, returning the string selected. Otherwise, throw an exception.
   */
  public String expect(StringBuilder buf, String[] cpss) {}

  /**
   * Read exactly one code point. If it matches the given predicate, then add it to the given buffer
   * and return. Otherwise, unread the code point and throw an exception. exception.
   */
  public void expect(StringBuilder buf, IntPredicate p) {}

  public int take(StringBuilder buf, IntPredicate p) {}
}
