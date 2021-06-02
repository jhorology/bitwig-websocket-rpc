/*
 * Copyright (c) 2019 Masafumi Fujimaru
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.ext.impl;

import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.github.jhorology.bitwig.ext.IdValuePair;
import com.github.jhorology.bitwig.ext.api.ObservedDirectParameterValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of Value that reports observed direct parameter.
 * @param <T> type of raw value.
 */
public class ObservedDirectParameterValueImpl<T>
  implements ObservedDirectParameterValue<T> {

  private final Map<String, IdValuePair<String, T>> paramsCache;
  private List<String> observedIds;
  private final List<ObjectValueChangedCallback<IdValuePair<String, T>>> callbacks;
  private int subscribeCount;
  private final boolean notifyValuesOnSetIds;

  /**
   * Constructor.
   * @param name
   */
  ObservedDirectParameterValueImpl(boolean notifyValuesOnSetIds) {
    this.paramsCache = new LinkedHashMap<>();
    this.callbacks = new ArrayList<>();
    this.notifyValuesOnSetIds = notifyValuesOnSetIds;
    this.observedIds = Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setObservedIds(String[] ids) {
    this.observedIds =
      ids != null ? Arrays.asList(ids) : Collections.emptyList();
    if (isSubscribed() && notifyValuesOnSetIds) {
      notifyValues();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T get(String id) {
    IdValuePair<String, T> v = paramsCache.get(id);
    return v != null ? v.getValue() : null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<IdValuePair<String, T>> values() {
    return observedIds
      .stream()
      .map(paramsCache::get)
      .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markInterested() {
    subscribe();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSubscribed() {
    return subscribeCount > 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Deprecated
  public void setIsSubscribed(boolean subscribed) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void subscribe() {
    subscribeCount++;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unsubscribe() {
    if (subscribeCount > 0) {
      subscribeCount--;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addValueObserver(
    ObjectValueChangedCallback<IdValuePair<String, T>> callback
  ) {
    callbacks.add(callback);
  }

  /**
   * Notify observed device is changed
   */
  void deviceChanged() {
    paramsCache.clear();
  }

  /**
   * Notify observed values to observers.
   */
  void notifyValues() {
    values().forEach(this::valueChanged);
  }

  /**
   * put or update direct parameter value.
   * @param id
   * @param value
   */
  void put(String id, T value) {
    IdValuePair<String, T> v = paramsCache.get(id);
    if (v != null) {
      v.setValue(value);
    } else {
      v = new IdValuePair<>(id, value);
      paramsCache.put(id, v);
    }
    if (isSubscribed() && observedIds.contains(id)) {
      valueChanged(v);
    }
  }

  private void valueChanged(IdValuePair<String, T> v) {
    callbacks.forEach(cb -> cb.valueChanged(v));
  }
}
