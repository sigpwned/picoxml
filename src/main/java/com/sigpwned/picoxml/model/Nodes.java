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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Nodes implements List<Node> {
  public static final Nodes EMPTY = new Nodes(emptyList());

  public static Nodes of(List<Node> nodes) {
    return nodes.isEmpty() ? EMPTY : new Nodes(nodes);
  }

  private final List<Node> delegate;

  public Nodes(List<Node> delegate) {
    this.delegate = unmodifiableList(delegate);
  }

  @Override
  public void add(int index, Node element) {
    getDelegate().add(index, element);
  }

  @Override
  public boolean add(Node e) {
    return getDelegate().add(e);
  }

  @Override
  public boolean addAll(Collection<? extends Node> c) {
    return getDelegate().addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Node> c) {
    return getDelegate().addAll(index, c);
  }

  @Override
  public void clear() {
    getDelegate().clear();
  }

  @Override
  public boolean contains(Object o) {
    return getDelegate().contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return getDelegate().containsAll(c);
  }

  @Override
  public boolean equals(Object o) {
    return getDelegate().equals(o);
  }

  @Override
  public void forEach(Consumer<? super Node> action) {
    getDelegate().forEach(action);
  }

  @Override
  public Node get(int index) {
    return getDelegate().get(index);
  }

  @Override
  public int hashCode() {
    return getDelegate().hashCode();
  }

  @Override
  public int indexOf(Object o) {
    return getDelegate().indexOf(o);
  }

  @Override
  public boolean isEmpty() {
    return getDelegate().isEmpty();
  }

  @Override
  public Iterator<Node> iterator() {
    return getDelegate().iterator();
  }

  @Override
  public int lastIndexOf(Object o) {
    return getDelegate().lastIndexOf(o);
  }

  @Override
  public ListIterator<Node> listIterator() {
    return getDelegate().listIterator();
  }

  @Override
  public ListIterator<Node> listIterator(int index) {
    return getDelegate().listIterator(index);
  }

  @Override
  public Stream<Node> parallelStream() {
    return getDelegate().parallelStream();
  }

  @Override
  public Node remove(int index) {
    return getDelegate().remove(index);
  }

  @Override
  public boolean remove(Object o) {
    return getDelegate().remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return getDelegate().removeAll(c);
  }

  @Override
  public boolean removeIf(Predicate<? super Node> filter) {
    return getDelegate().removeIf(filter);
  }

  @Override
  public void replaceAll(UnaryOperator<Node> operator) {
    getDelegate().replaceAll(operator);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return getDelegate().retainAll(c);
  }

  @Override
  public Node set(int index, Node element) {
    return getDelegate().set(index, element);
  }

  @Override
  public int size() {
    return getDelegate().size();
  }

  @Override
  public void sort(Comparator<? super Node> c) {
    getDelegate().sort(c);
  }

  @Override
  public Spliterator<Node> spliterator() {
    return getDelegate().spliterator();
  }

  @Override
  public Stream<Node> stream() {
    return getDelegate().stream();
  }

  @Override
  public List<Node> subList(int fromIndex, int toIndex) {
    return getDelegate().subList(fromIndex, toIndex);
  }

  @Override
  public Object[] toArray() {
    return getDelegate().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return getDelegate().toArray(a);
  }

  @Override
  public String toString() {
    if (isEmpty())
      return "[]";
    return "[" + stream().map(Objects::toString).collect(joining(", ")) + "]";
  }

  private List<Node> getDelegate() {
    return delegate;
  }
}
