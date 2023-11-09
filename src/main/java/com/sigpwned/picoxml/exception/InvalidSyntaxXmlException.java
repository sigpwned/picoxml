package com.sigpwned.picoxml.exception;

import com.sigpwned.picoxml.XmlException;

public class InvalidSyntaxXmlException extends XmlException {
  private static final long serialVersionUID = 4382314396316176696L;

  private final int lineNumber;
  private final int columnNumber;

  public InvalidSyntaxXmlException(int lineNumber, int columnNumber) {
    super("Syntax error on line " + lineNumber + " column " + columnNumber);
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public int getColumnNumber() {
    return columnNumber;
  }
}
