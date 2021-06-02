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
import com.bitwig.extension.controller.api.StringArrayValue;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Value that reports all of device parameter id.
 */
public class DirectParameterIdArrayValueImpl implements StringArrayValue {

  private int subscribeCount;
  private String[] ids;
  private final List<ObjectValueChangedCallback<String[]>> callbacks;

  /**
   * Constructor.
   * @param device
   */
  DirectParameterIdArrayValueImpl() {
    ids = new String[0];
    this.callbacks = new ArrayList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] get() {
    return ids;
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
    if (subscribeCount == 1 && ids != null) {
      valueChanged(ids);
    }
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
  public void addValueObserver(ObjectValueChangedCallback<String[]> callback) {
    callbacks.add(callback);
  }

  /**
   * notify all parameter ids to observers.
   * @param ids
   */
  void valueChanged(String[] ids) {
    this.ids = ids != null ? ids : new String[0];
    if (isSubscribed()) {
      callbacks.forEach(cb -> cb.valueChanged(ids));
    }
    this.ids = ids;
  }
}
