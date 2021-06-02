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

import com.bitwig.extension.controller.api.Clip;
//#if bitwig.extension.api.version >= 10
import com.bitwig.extension.controller.api.NoteStep;
//#endif
import com.github.jhorology.bitwig.ext.NoteStepState;
import com.github.jhorology.bitwig.ext.api.ClipExt;
import com.github.jhorology.bitwig.ext.api.CollectionValue;

/**
 * an implementation of extended Clip API.
 */
class ClipExtImpl implements ClipExt {

  private final NoteStepStateValueImpl noteStepState;
  //#if bitwig.extension.api.version >= 10
  private final NoteStepValueImpl noteStep;

  //#endif

  /**
   * Constructor.
   * @param device
   */
  ClipExtImpl(Clip clip, int gridWidth, int gridHeight) {
    noteStepState = new NoteStepStateValueImpl(clip, gridWidth, gridHeight);
    //#if bitwig.extension.api.version >= 10
    noteStep = new NoteStepValueImpl(clip, gridWidth, gridHeight);
    //#endif
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CollectionValue<NoteStepState> noteStepState() {
    return noteStepState;
  }

  //#if bitwig.extension.api.version >= 10
  /**
   * {@inheritDoc}
   */
  @Override
  public CollectionValue<NoteStep> noteStep() {
    return noteStep;
  }
  //#endif

}
