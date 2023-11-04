package com.sigpwned.picoxml.util;

public final class CharSequences {
  private CharSequences() {}

  public static boolean startsWith(CharSequence haystack, CharSequence needle) {
    if (needle.length() > haystack.length())
      return false;

    for (int i = 0; i < needle.length(); i++)
      if (haystack.charAt(i) != needle.charAt(i))
        return false;

    return true;
  }

  public static boolean endsWith(CharSequence haystack, CharSequence needle) {
    if (needle.length() > haystack.length())
      return false;

    int hlen = haystack.length();
    int nlen = needle.length();
    for (int i = 0; i < needle.length(); i++)
      if (haystack.charAt(hlen - nlen + i) != needle.charAt(i))
        return false;

    return true;
  }
}
