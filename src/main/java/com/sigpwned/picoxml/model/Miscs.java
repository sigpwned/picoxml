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

public class Miscs implements List<Misc> {
  public static final Miscs EMPTY = new Miscs(emptyList());

  public static Miscs of(List<Misc> miscs) {
    return miscs.isEmpty() ? EMPTY : new Miscs(miscs);
  }

  private final List<Misc> delegate;

  public Miscs(List<Misc> delegate) {
    this.delegate = unmodifiableList(delegate);
  }

  @Override
  public void add(int index, Misc element) {
    getDelegate().add(index, element);
  }

  @Override
  public boolean add(Misc e) {
    return getDelegate().add(e);
  }

  @Override
  public boolean addAll(Collection<? extends Misc> c) {
    return getDelegate().addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Misc> c) {
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
  public void forEach(Consumer<? super Misc> action) {
    getDelegate().forEach(action);
  }

  @Override
  public Misc get(int index) {
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
  public Iterator<Misc> iterator() {
    return getDelegate().iterator();
  }

  @Override
  public int lastIndexOf(Object o) {
    return getDelegate().lastIndexOf(o);
  }

  @Override
  public ListIterator<Misc> listIterator() {
    return getDelegate().listIterator();
  }

  @Override
  public ListIterator<Misc> listIterator(int index) {
    return getDelegate().listIterator(index);
  }

  @Override
  public Stream<Misc> parallelStream() {
    return getDelegate().parallelStream();
  }

  @Override
  public Misc remove(int index) {
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
  public boolean removeIf(Predicate<? super Misc> filter) {
    return getDelegate().removeIf(filter);
  }

  @Override
  public void replaceAll(UnaryOperator<Misc> operator) {
    getDelegate().replaceAll(operator);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return getDelegate().retainAll(c);
  }

  @Override
  public Misc set(int index, Misc element) {
    return getDelegate().set(index, element);
  }

  @Override
  public int size() {
    return getDelegate().size();
  }

  @Override
  public void sort(Comparator<? super Misc> c) {
    getDelegate().sort(c);
  }

  @Override
  public Spliterator<Misc> spliterator() {
    return getDelegate().spliterator();
  }

  @Override
  public Stream<Misc> stream() {
    return getDelegate().stream();
  }

  @Override
  public List<Misc> subList(int fromIndex, int toIndex) {
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

  private List<Misc> getDelegate() {
    return delegate;
  }
}
