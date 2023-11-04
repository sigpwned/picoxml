package com.sigpwned.picoxml.util;

public final class XmlChars {
  private XmlChars() {}

  /**
   * Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
   *
   * https://www.w3.org/TR/REC-xml/#NT-Char
   */
  public static boolean character(int cp) {
    return cp == '\t' || cp == '\r' || cp == '\n' || (cp >= 0x20 && cp <= 0xD7FF)
        || (cp >= 0xE000 && cp <= 0xFFFD) || (cp >= 0x10000 && cp <= 0x10FFFF);
  }

  /**
   * S ::= (#x20 | #x9 | #xD | #xA)+
   *
   * https://www.w3.org/TR/REC-xml/#NT-S
   */
  public static boolean s(int cp) {
    return cp == ' ' || cp == '\t' || cp == '\r' || cp == '\n';
  }

  /**
   * Eq ::= S? '=' S?
   *
   * https://www.w3.org/TR/REC-xml/#NT-Eq
   */
  public static boolean eq(int cp) {
    return cp == '=';
  }

  /**
   * NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] |
   * [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] |
   * [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
   *
   * https://www.w3.org/TR/REC-xml/#NT-NameStartChar
   */
  public static boolean nameStartChar(int cp) {
    return cp == ':' || cp == '_' || (cp >= 'A' && cp <= 'Z') || (cp >= 'a' && cp <= 'z')
        || (cp >= 0xC0 && cp <= 0xD6) || (cp >= 0xD8 && cp <= 0xF6) || (cp >= 0xF8 && cp <= 0x2FF)
        || (cp >= 0x370 && cp <= 0x37D) || (cp >= 0x37F && cp <= 0x1FFF)
        || (cp >= 0x200C && cp <= 0x200D) || (cp >= 0x2070 && cp <= 0x218F)
        || (cp >= 0x2C00 && cp <= 0x2FEF) || (cp >= 0x3001 && cp <= 0xD7FF)
        || (cp >= 0xF900 && cp <= 0xFDCF) || (cp >= 0xFDF0 && cp <= 0xFFFD)
        || (cp >= 0x10000 && cp <= 0xEFFFF);
  }

  /**
   * NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
   *
   * https://www.w3.org/TR/REC-xml/#NT-NameChar
   */
  public static boolean nameChar(int cp) {
    return cp == '-' || cp == '.' || (cp >= '0' && cp <= '9') || cp == 0xB7
        || (cp >= 0x300 && cp <= 0x36F) || (cp >= 0x203F && cp <= 0x2040) || nameStartChar(cp);
  }

  /**
   * [0-9]
   */
  public static boolean digit(int cp) {
    return cp >= '0' && cp <= '9';
  }

  /**
   * [0-9a-fA-F]
   */
  public static boolean hexdigit(int cp) {
    return (cp >= 'a' && cp <= 'f') || (cp >= 'A' && cp <= 'F') || digit(cp);
  }

  /**
   * PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
   *
   * https://www.w3.org/TR/REC-xml/#NT-PubidChar
   */
  public static boolean pubidChar(int cp) {
    return cp == ' ' || cp == '\r' || cp == '\n' || hexdigit(cp)
        || "-'()+,./:=?;!*#@$_%".codePoints().anyMatch(cpi -> cp == cpi);
  }
}
