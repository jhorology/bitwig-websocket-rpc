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

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

/**
 * JSON serializable raw value object class for state of grid element.
 */
public class NoteStepState {

  @Expose
  private final int x;

  @Expose
  private final int y;

  @Expose
  private int state;

  /**
   * Constructor.
   * @param x the x (step) coordinate within the note grid (integer).
   * @param y the y (key) coordinate within the note grid (integer).
   */
  public NoteStepState(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the x (step) coordinate within the note grid (integer).
   * @return
   */
  public int getX() {
    return x;
  }

  /**
   * Return the y (key) coordinate within the note grid (integer).
   * @return
   */
  public int getY() {
    return y;
  }

  /**
   * Returns an integer value that indicates if the step is empty (0) or
   * if a note continues playing (1) or starts playing (2).
   * @return
   */
  public int getState() {
    return state;
  }

  /**
   * Sets an integer value that indicates if the step is empty (0) or
   * if a note continues playing (1) or starts playing (2).
   * @param state
   */
  public void setState(int state) {
    this.state = state;
  }
}
