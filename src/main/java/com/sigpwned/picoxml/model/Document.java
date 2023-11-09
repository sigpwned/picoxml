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
