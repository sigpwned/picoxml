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
package com.sigpwned.picoxml.model.node.ref;

import java.util.Objects;
import com.sigpwned.picoxml.model.node.Ref;

public class CharRef extends Ref {
  public static final int DEC = 10;
  public static final int HEX = 16;


  private final int base;
  private final String digits;

  public CharRef(int base, String digits) {
    if (base != DEC && base != HEX)
      throw new IllegalArgumentException("base must be " + DEC + " or " + HEX);
    if (digits == null)
      throw new NullPointerException();
    this.base = base;
    this.digits = digits;
  }

  public int getBase() {
    return base;
  }

  public String getDigits() {
    return digits;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(base, digits);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CharRef other = (CharRef) obj;
    return base == other.base && Objects.equals(digits, other.digits);
  }

  @Override
  public String toString() {
    return "CharRef [base=" + base + ", digits=" + digits + "]";
  }
}
