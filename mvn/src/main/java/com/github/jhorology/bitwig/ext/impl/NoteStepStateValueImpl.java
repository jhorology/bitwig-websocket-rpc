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
import com.bitwig.extension.controller.api.Clip;
import com.github.jhorology.bitwig.ext.NoteStepState;
import com.github.jhorology.bitwig.ext.api.CollectionValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * an implementation of extended Clip API.
 */
class NoteStepStateValueImpl implements CollectionValue<NoteStepState> {

  private final List<ObjectValueChangedCallback<NoteStepState>> callbacks;
  private final Map<ImmutablePair<Integer, Integer>, NoteStepState> values;
  private int subscribeCount;

  /**
   * Constructor.
   * @param clip the instance or Clip API.
   */
  NoteStepStateValueImpl(Clip clip, int gridWidth, int gridHeight) {
    callbacks = new ArrayList<>();
    values = new HashMap<>();
    clip.addStepDataObserver(this::notifyNoteStepState);
  }

  @Override
  public Collection<NoteStepState> values() {
    return values.values();
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
  public void addValueObserver(
    ObjectValueChangedCallback<NoteStepState> callback
  ) {
    callbacks.add(callback);
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

  private void notifyNoteStepState(int x, int y, int state) {
    ImmutablePair<Integer, Integer> key = ImmutablePair.of(x, y);
    NoteStepState v = values.get(key);
    if (v == null) {
      v = new NoteStepState(x, y);
      values.put(key, v);
    }
    v.setState(state);
    if (isSubscribed()) {
      final NoteStepState value = v;
      callbacks.forEach(c -> c.valueChanged(value));
    }
  }
}
