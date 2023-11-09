package com.sigpwned.picoxml;

public class XmlException extends RuntimeException {
  private static final long serialVersionUID = 1285666017082607348L;

  public XmlException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public XmlException(String arg0) {
    super(arg0);
  }

  public XmlException(Throwable arg0) {
    super(arg0);
  }
}
