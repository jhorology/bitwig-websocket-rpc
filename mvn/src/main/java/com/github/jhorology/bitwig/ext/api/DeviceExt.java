/*
 * Copyright (c) 2018 Masafumi Fujimaru
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
package com.github.jhorology.bitwig.ext.api;

import com.bitwig.extension.controller.api.StringArrayValue;

/**
 * The extended API that allows to access direct parameters of Device.
 */
public interface DeviceExt {
  /**
   * Value that reports the all parameter ids of the device.
   * @return
   */
  StringArrayValue directParameterIdArray();

  /**
   * Starts observing the parameters according to the given parameter ID array,
   * or stops observing in case null is passed in for the parameter ID array.
   * @param ids
   */
  void setObservedDirectParameterIds(String[] ids);

  /**
   * Value that reports the observed parameter name of the device.
   * @return
   */
  ObservedDirectParameterValue<String> observedDirectParameterName();

  /**
   * Value that reports the observed parameter value formatted as string.
   * @return
   */
  ObservedDirectParameterValue<String> observedDirectParameterDisplayValue();

  /**
   * Value that reports the observed parameter value normalized as 0..1.
   * @return
   */
  ObservedDirectParameterValue<Double> observedDirectParameterNormalizedValue();
}
