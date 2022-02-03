/*
 * This source code is based on https://github.com/qos-ch/slf4j/blob/v_1.7.25/slf4j-simple/src/main/java/org/slf4j/impl/StaticMarkerBinder.java
 */

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
package com.github.jhorology.bitwig.logging.impl;

// dependencies
import org.slf4j.IMarkerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * The binding of {@link MarkerFactory} class with an actual instance of
 * {@link IMarkerFactory} is performed using information returned by this class.
 */
public class StaticMarkerBinder implements MarkerFactoryBinder {

  /**
   * The unique instance of this class.
   */
  public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

  final IMarkerFactory markerFactory = new BasicMarkerFactory();

  private StaticMarkerBinder() {}

  /**
   * Return the singleton of this class.
   *
   * @return the StaticMarkerBinder singleton
   * @since 1.7.14
   */
  public static StaticMarkerBinder getSingleton() {
    return SINGLETON;
  }

  /**
   * Currently this method always returns an instance of
   * {@link BasicMarkerFactory}.
   */
  public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }

  /**
   * Currently, this method returns the class name of
   * {@link BasicMarkerFactory}.
   */
  public String getMarkerFactoryClassStr() {
    return BasicMarkerFactory.class.getName();
  }
}
