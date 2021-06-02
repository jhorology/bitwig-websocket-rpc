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
package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

public class ExcludeFieldsWithoutExposeAnnotationStrategy
  implements ExclusionStrategy {

  private final boolean serialize;

  /**
   * Constructor.
   * @param serialize classfied as either serialize or deserialize, serailize = true.
   */
  public ExcludeFieldsWithoutExposeAnnotationStrategy(boolean serialize) {
    this.serialize = serialize;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldSkipField(FieldAttributes fa) {
    Class<?> clazz = fa.getDeclaringClass();
    // if Bitwig API or class of this package
    if (
      clazz.getName().startsWith("com.bitwig.") ||
      clazz
        .getName()
        .startsWith("com.github.jhorology.bitwig.websocket.protocol.jsonrpc.")
    ) {
      return false;
    }
    Expose annotation = fa.getAnnotation(Expose.class);
    if (
      annotation == null ||
      (serialize ? !annotation.serialize() : !annotation.deserialize())
    ) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldSkipClass(Class<?> type) {
    return false;
  }
}
