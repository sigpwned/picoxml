package com.sigpwned.picoxml;

import java.util.OptionalInt;
import com.sigpwned.picoxml.util.XmlChars;

/**
 * https://www.w3.org/TR/REC-xml/
 */
public class XmlParser {
  private XmlInputStream in;
  private StringBuilder buf = new StringBuilder();

  public void parse() {
    if (in.attempt("<?")) {
    } else if (in.attempt("<!")) {
      if (in.attempt("[CDATA[")) {
      } else if (in.attempt("--")) {
      } else {
      }
    } else if (in.attempt("</")) {
    } else if (in.attempt("<")) {
    }
  }

  /**
   * [23]
   *
   * XMLDecl ::= '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
   *
   * https://www.w3.org/TR/REC-xml/#NT-XMLDecl
   */
  private String xmlDecl() {
    buf.setLength(0);

    in.expect(buf, "<?xml");

    in.take(buf, XmlChars::s);
    if (in.lookahead("version"))
      versionInfo(buf);

    in.take(buf, XmlChars::s);
    if (in.lookahead("encoding"))
      encodingDecl(buf);

    in.take(buf, XmlChars::s);
    if (in.lookahead("standalone"))
      sdDecl(buf);

    in.take(buf, XmlChars::s);

    in.expect(buf, "?>");

    return buf.toString();
  }

  /**
   * [24]
   *
   * VersionInfo ::= S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
   *
   * https://www.w3.org/TR/REC-xml/#NT-VersionInfo
   */
  private void versionInfo(StringBuilder buf) {
    in.expect(buf, "version");
    in.take(buf, XmlChars::s);
    in.expect(buf, XmlChars::eq);
    in.take(buf, XmlChars::s);

    int quote = in.expect(buf, QUOTES);
    versionNum(buf);
    in.expect(buf, cp -> cp == quote);


  }

  /**
   * [26]
   *
   * VersionNum ::= '1.' [0-9]+
   *
   * https://www.w3.org/TR/REC-xml/#NT-VersionNum
   */
  private void versionNum(StringBuilder buf) {
    in.expect(buf, cp -> cp == '1');
    in.expect(buf, cp -> cp == '.');
    in.expect(buf, XmlChars::digit);
    in.take(buf, XmlChars::digit);
  }

  /**
   * Misc *
   */
  private void miscs(StringBuilder buf) {
    in.take(buf, XmlChars::s);
    while (in.lookahead("<?") || in.lookahead("<!--")) {
      misc(buf);
      in.take(buf, XmlChars::s);
    }
  }

  /**
   * [27]
   *
   * Misc ::= Comment | PI | S
   *
   * https://www.w3.org/TR/REC-xml/#NT-Misc
   */
  private void misc(StringBuilder buf) {
    in.take(buf, XmlChars::s);
    if (in.lookahead("<?")) {
      pi(buf);
    } else if (in.lookahead("<!--")) {
      comment(buf);
    }
  }


  /**
   * [28]
   *
   * doctypedecl ::= '<!DOCTYPE' S Name (S ExternalID)? S? ('[' intSubset ']' S?)? '>'
   *
   * https://www.w3.org/TR/REC-xml/#NT-doctypedecl
   */
  private void doctypedecl() {
    buf.setLength(0);

    in.expect(buf, "<!DOCTYPE");

    in.expect(buf, XmlChars::s);
    in.take(buf, XmlChars::s);
    name(buf);

    in.take(buf, XmlChars::s);
    if (in.lookahead("SYSTEM") || in.lookahead("PUBLIC"))
      externalId(buf);

    in.take(buf, XmlChars::s);

  }

  /**
   * [28a]
   *
   * DeclSep ::= PEReference | S
   *
   * https://www.w3.org/TR/REC-xml/#NT-DeclSep
   */
  private void declSep(StringBuilder buf) {
    if (in.lookahead("%")) {
      pereference(buf);
    } else {
      in.expect(buf, XmlChars::s);
      in.take(buf, XmlChars::s);
    }
  }

  /**
   * [28b]
   *
   * intSubset ::= (markupdecl | DeclSep)*
   *
   * https://www.w3.org/TR/REC-xml/#NT-intSubset
   */
  private void intSubset(StringBuilder buf) {
    markupdecl(buf);
  }

  /**
   * [29]
   *
   * markupdecl ::= elementdecl | AttlistDecl | EntityDecl | NotationDecl | PI | Comment
   *
   * https://www.w3.org/TR/REC-xml/#NT-markupdecl
   */
  private void markupdecl(StringBuilder buf) {
    if (in.lookahead("<!ELEMENT")) {
      elementdecl(buf);
    } else {
      in.expect(buf, XmlChars::s);
      in.take(buf, XmlChars::s);
    }
  }

  /**
   * EncodingDecl ::= S 'encoding' Eq ('"' EncName '"' | "'" EncName "'" )
   *
   * https://www.w3.org/TR/REC-xml/#NT-EncodingDecl
   */
  private void encodingDecl(StringBuilder buf) {
    in.expect(buf, "encoding");
    in.take(buf, XmlChars::s);
    in.expect(buf, XmlChars::eq);
    in.take(buf, XmlChars::s);

    int quote = in.expect(buf, QUOTES);
    encName(buf);
    in.expect(buf, cp -> cp == quote);
  }

  /**
   * EncName ::= [A-Za-z] ([A-Za-z0-9._] | '-')*
   *
   * https://www.w3.org/TR/REC-xml/#NT-EncName
   */
  private void encName(StringBuilder buf) {
    in.expect(buf, cp -> (cp >= 'a' && cp <= 'z') || (cp >= 'A' && cp <= 'Z'));
    in.take(buf, cp -> (cp >= 'a' && cp <= 'z') || (cp >= 'A' && cp <= 'Z')
        || (cp >= '0' && cp <= '9') || cp == '.' || cp == '_' || cp == '-');
  }

  private static final String[] YES_NO = new String[] {"yes", "no"};

  /**
   * SDDecl ::= S 'standalone' Eq (("'" ('yes' | 'no') "'") | ('"' ('yes' | 'no') '"'))
   *
   * https://www.w3.org/TR/REC-xml/#NT-SDDecl
   */
  private void sdDecl(StringBuilder buf) {
    in.expect(buf, "standalone");
    in.take(buf, XmlChars::s);
    in.expect(buf, XmlChars::eq);
    in.take(buf, XmlChars::s);

    int quote = in.expect(buf, QUOTES);
    in.expect(buf, YES_NO);
    in.expect(buf, cp -> cp == quote);
  }

  /**
   * Comment ::= '<!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
   *
   * https://www.w3.org/TR/REC-xml/#NT-Comment
   */
  private void comment(StringBuilder buf) {
    in.expect(buf, "<!--");
    while (!in.lookahead("--"))
      in.expect(buf, XmlChars::character);
    in.expect(buf, "-->");
  }

  /**
   * [45]
   *
   * elementdecl ::= '<!ELEMENT' S Name S contentspec S? '>'
   *
   * https://www.w3.org/TR/REC-xml/#NT-elementdecl
   */
  private void elementdecl(StringBuilder buf) {
    in.expect(buf, "<!ELEMENT");

    in.expect(buf, XmlChars::s);
    in.take(buf, XmlChars::s);
    name(buf);

    in.expect(buf, XmlChars::s);
    in.take(buf, XmlChars::s);
    contentspec(buf);

    in.take(buf, XmlChars::s);

    in.expect(buf, '>');
  }

  /**
   * [46]
   *
   * contentspec ::= 'EMPTY' | 'ANY' | Mixed | children
   *
   * https://www.w3.org/TR/REC-xml/#NT-contentspec
   */
  private void contentspec(StringBuilder buf) {
    if (in.attempt(buf, "EMPTY")) {
      // done
    } else if (in.attempt(buf, "ANY")) {
      // done
    } else if (1) {
      // Mixed ::= '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*' | '(' S? '#PCDATA' S? ')'
    }
  }

  private static final int[] QUESTION_STAR_PLUS = new int[] {'?', '*', '+'};

  /**
   * [47]
   *
   * children ::= (choice | seq) ('?' | '*' | '+')?
   *
   * https://www.w3.org/TR/REC-xml/#NT-children
   */
  private void children(StringBuilder buf) {
    choiceOrSeq(buf);
    OptionalInt maybeSuffix = in.attempt(buf, QUESTION_STAR_PLUS);
  }

  /**
   * [48]
   *
   * cp ::= (Name | choice | seq) ('?' | '*' | '+')?
   *
   * https://www.w3.org/TR/REC-xml/#NT-cp
   */
  private void cp(StringBuilder buf) {
    if (in.lookahead('(')) {
      choiceOrSeq(buf);
    } else {
      name(buf);
    }

    OptionalInt maybeSuffix = in.attempt(buf, QUESTION_STAR_PLUS);
  }

  private static final int[] PIPE_COMMA = new int[] {'|', ','};

  /**
   * [49]
   *
   * choice ::= '(' S? cp ( S? '|' S? cp )+ S? ')'
   *
   * https://www.w3.org/TR/REC-xml/#NT-choice
   *
   * [50]
   *
   * seq ::= '(' S? cp ( S? ',' S? cp )* S? ')'
   *
   * https://www.w3.org/TR/REC-xml/#NT-seq
   */
  private void choiceOrSeq(StringBuilder buf) {
    in.expect(buf, '(');

    in.take(buf, XmlChars::s);

    cp(buf);

    in.take(buf, XmlChars::s);

    if (in.lookahead(')')) {
      // If there's no pipe or comma, then it doesn't matter what we are.
    } else {
      // if sep = '|', then it's a choice.
      // if sep = ',', then it's a seq.
      int sep = in.expect(buf, PIPE_COMMA);
      in.take(buf, XmlChars::s);
      cp(buf);
      in.take(buf, XmlChars::s);
      while (in.attempt(buf, sep)) {
        in.take(buf, XmlChars::s);
        cp(buf);
        in.take(buf, XmlChars::s);
      }
      in.take(buf, XmlChars::s);
    }

    in.expect(buf, ')');
  }

  private void mixed(StringBuilder buf) {
    in.expect(buf, '(');

    in.take(buf, XmlChars::s);
    in.expect(buf, "#PCDATA");

    in.take(buf, XmlChars::s);
    while (in.attempt(buf, '|')) {
      in.take(buf, XmlChars::s);
      name(buf);
      in.take(buf, XmlChars::s);
    }

    in.take(buf, XmlChars::s);

    in.expect(buf, ")");

    in.attempt(buf, '*');
  }

  /**
   * ExternalID ::= 'SYSTEM' S SystemLiteral | 'PUBLIC' S PubidLiteral S SystemLiteral
   *
   * https://www.w3.org/TR/REC-xml/#NT-ExternalID
   */
  private void externalId(StringBuilder buf) {
    if (in.attempt(buf, "SYSTEM")) {
      in.expect(buf, XmlChars::s);
      in.take(buf, XmlChars::s);
      systemLiteral(buf);
    } else if (in.attempt(buf, "PUBLIC")) {
      in.expect(buf, XmlChars::s);
      in.take(buf, XmlChars::s);
      pubidLiteral(buf);

      in.expect(buf, XmlChars::s);
      in.take(buf, XmlChars::s);
      systemLiteral(buf);
    }
  }

  private static final int[] QUOTES = new int[] {'\'', '\"'};

  /**
   * EntityValue ::= '"' ([^%&"] | PEReference | Reference)* '"' | "'" ([^%&'] | PEReference |
   * Reference)* "'"
   *
   * https://www.w3.org/TR/REC-xml/#NT-EntityValue
   */
  private String entityValue() {
    buf.setLength(0);

    int quote = in.expect(buf, QUOTES);
    while (!in.attempt(buf, quote)) {
      if (in.lookahead('&')) {
        reference(buf);
      } else if (in.lookahead('%')) {
        pereference(buf);
      } else {
        in.expect(buf, cp -> true);
      }
    }

    return buf.toString();
  }

  /**
   * AttValue ::= '"' ([^<&"] | Reference)* '"' | "'" ([^<&'] | Reference)* "'"
   *
   * https://www.w3.org/TR/REC-xml/#NT-AttValue
   */
  private String attValue() {
    buf.setLength(0);

    int quote = in.expect(buf, QUOTES);
    while (!in.attempt(buf, quote)) {
      if (in.lookahead('&')) {
        reference(buf);
      } else {
        in.expect(buf, cp -> true);
      }
    }

    return buf.toString();
  }

  /**
   * SystemLiteral ::= ('"' [^"]* '"') | ("'" [^']* "'")
   *
   * https://www.w3.org/TR/REC-xml/#NT-SystemLiteral
   */
  private void systemLiteral(StringBuilder buf) {
    int quote = in.expect(buf, QUOTES);
    while (!in.attempt(buf, quote)) {
      in.expect(buf, cp -> true);
    }
  }

  private void pubidLiteral(StringBuilder buf) {
    int quote = in.expect(buf, QUOTES);
    while (!in.attempt(buf, quote)) {
      in.expect(buf, XmlChars::pubidChar);
    }
  }

  /**
   * CharData ::= [^<&]* - ([^<&]* ']]>' [^<&]*)
   *
   * https://www.w3.org/TR/REC-xml/#NT-CharData
   */
  private String charData() {
    buf.setLength(0);

    if (in.lookahead("]]>"))
      return buf.toString();
    while (in.attempt(buf, cp -> cp != '<' && cp != '&')) {
      if (in.lookahead("]]>"))
        break;
    }

    return buf.toString();
  }

  private String pi() {
    buf.setLength(0);
    pi(buf);
    return buf.toString();
  }

  private void pi(StringBuilder buf) {
    in.expect(buf, "<?");
    piTarget(buf);
    in.take(buf, XmlChars::s);
    while (!in.lookahead("?>")) {
      in.expect(buf, cp -> XmlChars.character(cp));
    }
    in.expect(buf, "?>");
  }

  private void piTarget(StringBuilder buf) {
    String result = name();
    if (result.equalsIgnoreCase("xml")) {
      throw new RuntimeException("xml");
    }
    buf.append(result);
  }

  private String name() {
    buf.setLength(0);
    name(buf);
    return buf.toString();
  }

  private void name(StringBuilder buf) {
    in.expect(buf, XmlChars::nameStartChar);
    in.take(buf, XmlChars::nameChar);
  }

  private String nmtoken() {
    buf.setLength(0);
    in.expect(buf, XmlChars::nameChar);
    in.take(buf, XmlChars::nameChar);
    return buf.toString();
  }

  private String pereference() {
    buf.setLength(0);
    pereference(buf);
    return buf.toString();
  }

  /**
   * PEReference ::= '%' Name ';'
   *
   * https://www.w3.org/TR/REC-xml/#NT-PEReference
   */
  private void pereference(StringBuilder buf) {
    in.expect(buf, '%');
    name(buf);
    in.expect(buf, ';');
  }

  private String reference() {
    buf.setLength(0);
    if (in.lookahead("&#")) {
      charRef(buf);
    } else {
      entityRef(buf);
    }
    return buf.toString();
  }

  /**
   * Reference ::= EntityRef | CharRef
   *
   * https://www.w3.org/TR/REC-xml/#NT-Reference
   */
  private void reference(StringBuilder buf) {
    if (in.lookahead("&#")) {
      charRef(buf);
    } else {
      entityRef(buf);
    }
  }

  private String charRef() {
    buf.setLength(0);
    charRef(buf);
    return buf.toString();
  }

  /**
   * CharRef ::= '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';
   *
   * https://www.w3.org/TR/REC-xml/#NT-CharRef
   */
  private void charRef(StringBuilder buf) {
    in.expect(buf, "&#");
    if (in.attempt(buf, 'x')) {
      in.take(buf, XmlChars::hexdigit);
    } else {
      in.take(buf, XmlChars::digit);
    }
    in.expect(buf, ';');
  }

  private String entityRef() {
    buf.setLength(0);
    entityRef(buf);
    return buf.toString();
  }

  /**
   * EntityRef ::= '&' Name ';'
   *
   * https://www.w3.org/TR/REC-xml/#NT-EntityRef
   */
  private void entityRef(StringBuilder buf) {
    in.expect(buf, '&');
    name(buf);
    in.expect(buf, ';');
  }
}
