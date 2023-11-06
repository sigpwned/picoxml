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
package com.sigpwned.picoxml.model;

import java.util.Objects;
import com.sigpwned.picoxml.model.node.Element;

public class Document {
  private final Prolog prolog;
  private final Miscs beforeMiscs;
  private final Element root;
  private final Miscs afterMiscs;

  public Document(Prolog prolog, Miscs beforeMiscs, Element root, Miscs afterMiscs) {
    if (beforeMiscs == null)
      throw new NullPointerException();
    if (root == null)
      throw new NullPointerException();
    if (afterMiscs == null)
      throw new NullPointerException();
    this.prolog = prolog;
    this.beforeMiscs = beforeMiscs;
    this.root = root;
    this.afterMiscs = afterMiscs;
  }

  public Prolog getProlog() {
    return prolog;
  }

  public Miscs getBeforeMiscs() {
    return beforeMiscs;
  }

  public Element getRoot() {
    return root;
  }

  public Miscs getAfterMiscs() {
    return afterMiscs;
  }

  @Override
  public int hashCode() {
    return Objects.hash(afterMiscs, beforeMiscs, prolog, root);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Document other = (Document) obj;
    return Objects.equals(afterMiscs, other.afterMiscs)
        && Objects.equals(beforeMiscs, other.beforeMiscs) && Objects.equals(prolog, other.prolog)
        && Objects.equals(root, other.root);
  }

  @Override
  public String toString() {
    return "Document [prolog=" + prolog + ", beforeMiscs=" + beforeMiscs + ", root=" + root
        + ", afterMiscs=" + afterMiscs + "]";
  }
}
