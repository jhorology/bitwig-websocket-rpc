// TODO Action#isEnabled() is gone at 3.1 Beta 4.
//#excludeif bitwig.extension.api.version < 99
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
import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.BooleanValue;
import com.github.jhorology.bitwig.ext.IdValuePair;
import com.github.jhorology.bitwig.ext.api.ObservedActionValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation of Value that reports observed direct parameter.
 */
public class ObservedActionValueImpl implements ObservedActionValue {

  private final Application application;
  private final Map<String, IdValuePair<String, Boolean>> observedValues;
  private final List<ObjectValueChangedCallback<IdValuePair<String, Boolean>>> callbacks;
  private int subscribeCount = 0;

  /**
   * Constructor.
   * @param name
   */
  ObservedActionValueImpl(Application application) {
    this.application = application;
    this.callbacks = new ArrayList<>();
    this.observedValues = new HashMap<>();
    Stream
      .of(application.getActions())
      .forEach(action -> {
        action
          .isEnabled()
          .addValueObserver((boolean b) -> {
            IdValuePair<String, Boolean> v = observedValues.get(action.getId());
            if (v != null) {
              v.setValue(b);
              notifyValue(v);
            }
          });
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setObservedIds(String[] ids) {
    if (ids == null) {
      observedValues
        .values()
        .forEach(v -> {
          Action action = application.getAction(v.getId());
          if (action != null && action.isEnabled().isSubscribed()) {
            action.isEnabled().unsubscribe();
          }
        });
      observedValues.clear();
      return;
    }
    List<IdValuePair<String, Boolean>> dells = new ArrayList<>();
    List<String> observerdIds = Arrays.asList(ids);
    observedValues
      .values()
      .forEach(v -> {
        if (!observerdIds.contains(v.getId())) {
          dells.add(v);
        }
      });
    dells.forEach(v -> {
      Action action = application.getAction(v.getId());
      if (action != null && action.isEnabled().isSubscribed()) {
        action.isEnabled().unsubscribe();
      }
      observedValues.remove(v.getId());
    });

    observerdIds.forEach(id -> {
      if (!observedValues.keySet().contains(id)) {
        Action action = application.getAction(id);
        if (action != null) {
          action.isEnabled().subscribe();
          observedValues.put(
            id,
            new IdValuePair<>(id, action.isEnabled().get())
          );
        }
      }
    });

    if (isSubscribed()) {
      notifyValues();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEnabled(String id) {
    Action action = application.getAction(id);
    if (action != null) {
      return action.isEnabled().get();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<IdValuePair<String, Boolean>> values() {
    return observedValues.values();
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
      if (subscribeCount == 0) {
        setObservedIds(null);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addValueObserver(
    ObjectValueChangedCallback<IdValuePair<String, Boolean>> callback
  ) {
    callbacks.add(callback);
  }

  /**
   * Notify observed values to observers.
   */
  void notifyValues() {
    values().forEach(this::notifyValue);
  }

  private void notifyValue(IdValuePair<String, Boolean> v) {
    if (isSubscribed()) {
      callbacks.forEach(cb -> cb.valueChanged(v));
    }
  }
}
