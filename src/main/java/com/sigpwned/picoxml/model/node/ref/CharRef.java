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
