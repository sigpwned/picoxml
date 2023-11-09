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
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Attributes implements List<Attribute> {
  public static final Attributes EMPTY = new Attributes(emptyList());

  public static Attributes of(List<Attribute> attributes) {
    return attributes.isEmpty() ? EMPTY : new Attributes(attributes);
  }

  private final List<Attribute> delegate;

  public Attributes(List<Attribute> delegate) {
    this.delegate = unmodifiableList(delegate);
  }

  public Optional<Attribute> findAttributeByLocalName(String localName) {
    return stream().filter(n -> n.getLocalName().equals(localName)).findFirst();
  }

  public Optional<Attribute> findAttributeByPrefixAndLocalName(String prefix, String localName) {
    return stream()
        .filter(n -> n.getLocalName().equals(localName) && Objects.equals(n.getPrefix(), prefix))
        .findFirst();
  }

  @Override
  public boolean add(Attribute arg0) {
    return getDelegate().add(arg0);
  }

  @Override
  public void add(int arg0, Attribute arg1) {
    getDelegate().add(arg0, arg1);
  }

  @Override
  public boolean addAll(Collection<? extends Attribute> arg0) {
    return getDelegate().addAll(arg0);
  }

  @Override
  public boolean addAll(int arg0, Collection<? extends Attribute> arg1) {
    return getDelegate().addAll(arg0, arg1);
  }

  @Override
  public void clear() {
    getDelegate().clear();
  }

  @Override
  public boolean contains(Object arg0) {
    return getDelegate().contains(arg0);
  }

  @Override
  public boolean containsAll(Collection<?> arg0) {
    return getDelegate().containsAll(arg0);
  }

  @Override
  public boolean equals(Object arg0) {
    return getDelegate().equals(arg0);
  }

  @Override
  public void forEach(Consumer<? super Attribute> arg0) {
    getDelegate().forEach(arg0);
  }

  @Override
  public Attribute get(int arg0) {
    return getDelegate().get(arg0);
  }

  @Override
  public int hashCode() {
    return getDelegate().hashCode();
  }

  @Override
  public int indexOf(Object arg0) {
    return getDelegate().indexOf(arg0);
  }

  @Override
  public boolean isEmpty() {
    return getDelegate().isEmpty();
  }

  @Override
  public Iterator<Attribute> iterator() {
    return getDelegate().iterator();
  }

  @Override
  public int lastIndexOf(Object arg0) {
    return getDelegate().lastIndexOf(arg0);
  }

  @Override
  public ListIterator<Attribute> listIterator() {
    return getDelegate().listIterator();
  }

  @Override
  public ListIterator<Attribute> listIterator(int arg0) {
    return getDelegate().listIterator(arg0);
  }

  @Override
  public Stream<Attribute> parallelStream() {
    return getDelegate().parallelStream();
  }

  @Override
  public Attribute remove(int arg0) {
    return getDelegate().remove(arg0);
  }

  @Override
  public boolean remove(Object arg0) {
    return getDelegate().remove(arg0);
  }

  @Override
  public boolean removeAll(Collection<?> arg0) {
    return getDelegate().removeAll(arg0);
  }

  @Override
  public boolean removeIf(Predicate<? super Attribute> arg0) {
    return getDelegate().removeIf(arg0);
  }

  @Override
  public void replaceAll(UnaryOperator<Attribute> arg0) {
    getDelegate().replaceAll(arg0);
  }

  @Override
  public boolean retainAll(Collection<?> arg0) {
    return getDelegate().retainAll(arg0);
  }

  @Override
  public Attribute set(int arg0, Attribute arg1) {
    return getDelegate().set(arg0, arg1);
  }

  @Override
  public int size() {
    return getDelegate().size();
  }

  @Override
  public void sort(Comparator<? super Attribute> arg0) {
    getDelegate().sort(arg0);
  }

  @Override
  public Spliterator<Attribute> spliterator() {
    return getDelegate().spliterator();
  }

  @Override
  public Stream<Attribute> stream() {
    return getDelegate().stream();
  }

  @Override
  public List<Attribute> subList(int arg0, int arg1) {
    return getDelegate().subList(arg0, arg1);
  }

  @Override
  public Object[] toArray() {
    return getDelegate().toArray();
  }

  @Override
  public <T> T[] toArray(T[] arg0) {
    return getDelegate().toArray(arg0);
  }

  @Override
  public String toString() {
    if (isEmpty())
      return "[]";
    return "[" + stream().map(Objects::toString).collect(joining(", ")) + "]";
  }

  private List<Attribute> getDelegate() {
    return delegate;
  }
}
