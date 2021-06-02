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
package com.github.jhorology.bitwig.ext;

import com.google.gson.annotations.Expose;

/**
 * JSON serializable raw value object class for VU Meter
 * @author masafumi
 */
public class VuMeterLevel {

  @Expose
  private final int ch;

  @Expose
  private final boolean peak;

  @Expose
  private int value;

  /**
   * Return a integer value that mean channel as 0 = left 1=right -1=sum of left and right.
   * @param ch channel, 0=left 1=right -1=sum of left and right.
   * @param peak peak value or not.
   */
  public VuMeterLevel(int ch, boolean peak) {
    this.ch = ch;
    this.peak = peak;
  }

  /**
   * Return a integer value that mean channel as 0 = left 1=right -1=sum of left and right.
   * @return
   */
  public int getChannel() {
    return ch;
  }

  /**
   * Return this value is peek value or not.
   * @return peak or not
   */
  public boolean isPeak() {
    return peak;
  }

  /**
   * Return a meter level value that is ranged by configuration vuMeterRange.
   * @return ranged value.
   */
  public int getValue() {
    return value;
  }

  /**
   * Return a meter level value that is ranged by configuration vuMeterRange.
   * @param value ranged value.
   */
  public void setValue(int value) {
    this.value = value;
  }
}
