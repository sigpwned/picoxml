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
package com.sigpwned.picoxml.model.node;

import java.util.Objects;
import com.sigpwned.picoxml.model.Misc;
import com.sigpwned.picoxml.model.Node;
import com.sigpwned.picoxml.model.Nodes;

public class ProcessingInstruction extends Node implements Misc {
  private final String name;
  private final String content;

  public ProcessingInstruction(String name, String content) {
    super(Nodes.EMPTY);
    if (name == null)
      throw new NullPointerException();
    if (content == null)
      throw new NullPointerException();
    this.name = name;
    this.content = content;
  }

  public String getName() {
    return name;
  }

  public String getContent() {
    return content;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(content, name);
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
    ProcessingInstruction other = (ProcessingInstruction) obj;
    return Objects.equals(content, other.content) && Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "ProcessingInstruction [name=" + name + ", content=" + content + "]";
  }
}
