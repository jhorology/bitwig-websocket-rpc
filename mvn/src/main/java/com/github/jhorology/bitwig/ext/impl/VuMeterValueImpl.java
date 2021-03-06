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
import com.bitwig.extension.controller.api.Channel;
import com.github.jhorology.bitwig.ext.VuMeterLevel;
import com.github.jhorology.bitwig.ext.api.VuMeterChannelMode;
import com.github.jhorology.bitwig.ext.api.VuMeterPeakMode;
import com.github.jhorology.bitwig.ext.api.VuMeterValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VuMeterValueImpl implements VuMeterValue {

  private final Channel channel;
  private final List<VuMeterLevel> values;
  private final List<ObjectValueChangedCallback<VuMeterLevel>> callbacks;
  private int subscribeCount = 0;

  VuMeterValueImpl(
    Channel channel,
    int vuMeterRange,
    VuMeterChannelMode vuMeterChannelMode,
    VuMeterPeakMode vuMeterPeakMode
  ) {
    this.channel = channel;
    values = new ArrayList<>();
    this.callbacks = new ArrayList<>();
    if (vuMeterPeakMode.hasRMS()) {
      if (vuMeterChannelMode.hasMono()) {
        setupVuMeter(vuMeterRange, -1, false);
      }
      if (vuMeterChannelMode.hasLeft()) {
        setupVuMeter(vuMeterRange, 0, false);
      }
      if (vuMeterChannelMode.hasRight()) {
        setupVuMeter(vuMeterRange, 1, false);
      }
    }
    if (vuMeterPeakMode.hasPeak()) {
      if (vuMeterChannelMode.hasMono()) {
        setupVuMeter(vuMeterRange, -1, true);
      }
      if (vuMeterChannelMode.hasLeft()) {
        setupVuMeter(vuMeterRange, 0, true);
      }
      if (vuMeterChannelMode.hasRight()) {
        setupVuMeter(vuMeterRange, 1, true);
      }
    }
  }

  @Override
  public VuMeterLevel get(int ch, boolean peak) {
    return values
      .stream()
      .filter(m -> m.getChannel() == ch && m.isPeak() == peak)
      .findFirst()
      .orElse(null);
  }

  @Override
  public Collection<VuMeterLevel> values() {
    return values;
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

  @Override
  public void addValueObserver(
    ObjectValueChangedCallback<VuMeterLevel> callback
  ) {
    callbacks.add(callback);
  }

  private void setupVuMeter(int range, int ch, boolean peak) {
    VuMeterLevel meter = new VuMeterLevel(ch, peak);
    values.add(meter);
    channel.addVuMeterObserver(
      range,
      ch,
      peak,
      (int value) -> {
        meter.setValue(value);
        notifyValue(meter);
      }
    );
  }

  /**
   * Notify observed values to observers.
   */
  void notifyValues() {
    if (isSubscribed()) {
      values().forEach(this::notifyValue);
    }
  }

  private void notifyValue(VuMeterLevel v) {
    if (isSubscribed()) {
      callbacks.forEach(cb -> cb.valueChanged(v));
    }
  }
}
