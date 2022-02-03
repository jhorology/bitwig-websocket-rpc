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
package com.github.jhorology.bitwig.ext;

import com.bitwig.extension.controller.api.BeatTimeValue;
import com.google.gson.annotations.Expose;

/**
 * JSON serializable raw value object class that represents time line length or absolute position.
 */
public class BeatTime {

  @Expose
  private boolean absolute;

  @Expose
  private double raw;

  @Expose
  private int bars;

  @Expose
  private int beats;

  @Expose
  private int ticks;

  @Expose
  private int remainder;

  @Expose
  private int timeSignatureNumerator = 4;

  @Expose
  private int timeSignatureDenominator = 4;

  @Expose
  private int timeSignatureTicks = 16;

  /**
   * Constructer.
   * @param raw raw beat time value
   * @param beatTimeValue
   */
  public BeatTime(double raw, BeatTimeValue beatTimeValue) {
    update(raw, beatTimeValue);
  }

  /**
   * Update this instance.
   * @param raw raw beat time value
   * @param beatTimeValue
   */
  public void update(double raw, BeatTimeValue beatTimeValue) {
    if (raw == 0 || Double.isNaN(raw)) {
      this.raw = 0;
      bars = 1;
      beats = 1;
      ticks = 1;
    } else {
      this.raw = raw;
    }
    if (beatTimeValue.isSubscribed()) {
      String s = beatTimeValue.getFormatted();
      if (s != null && !"".equals(s.trim())) {
        String[] a = s.split(":");
        if (a.length == 4) {
          bars = Integer.parseInt(a[0]);
          beats = Integer.parseInt(a[1]);
          ticks = Integer.parseInt(a[2]);
          remainder = Integer.parseInt(a[3]);
        }
      }
      beatTimeValue.getFormatted(
        (double bt, boolean abs, int tsn, int tsd, int tst) -> {
          absolute = abs;
          timeSignatureNumerator = tsn;
          timeSignatureDenominator = tsd;
          timeSignatureTicks = tst;
          return null;
        }
      );
    }
  }

  /**
   * Returns a value for that represents an absolute time or not.
   * @return
   *   If true the beat time represents an absolute time (such as a time on the arranger)
   *   otherwise it represents a beat time duration (such as the length of a clip).
   */
  public boolean isAbsolute() {
    return absolute;
  }

  /**
   * Set a value for that represents an absolute time or not.
   * @param absolute
   *  If true the beat time represents an absolute time (such as a time on the arranger)
   *  otherwise it represents a beat time duration (such as the length of a clip).
   */
  public void setAbsolute(boolean absolute) {
    this.absolute = absolute;
  }

  /**
   * Returns a raw beat time value.
   * @return
   */
  public double getRaw() {
    return raw;
  }

  /**
   * Set a raw beat time value.
   * @param raw
   */
  public void setRaw(double raw) {
    this.raw = raw;
  }

  /**
   * Returns a current count of bars.
   * @return
   */
  public int getBars() {
    return bars;
  }

  /**
   * Set a current count of bars.
   * @param bars
   */
  public void setBars(int bars) {
    this.bars = bars;
  }

  /**
   * Returns a current count of beats within bar.
   * @return
   */
  public int getBeats() {
    return beats;
  }

  /**
   * Set a current count of beats within bar.
   * @param beats
   */
  public void setBeats(int beats) {
    this.beats = beats;
  }

  /**
   * Returns a current count of ticks within beat.
   * @return
   */
  public int getTicks() {
    return ticks;
  }

  /**
   * Set a current count of ticks within beat.
   * @param ticks
   */
  public void setTicks(int ticks) {
    this.ticks = ticks;
  }

  /**
   * Returns a current remainder percent of tick.
   * @return
   */
  public int getRemainder() {
    return remainder;
  }

  /**
   * Set a current remainder percent of tick.
   * @param remainder
   */
  public void setRemainder(int remainder) {
    this.remainder = remainder;
  }

  /**
   * Returns a numerator of current time-signature.
   * @return
   */
  public int geTimeSignatureNumerator() {
    return timeSignatureNumerator;
  }

  /**
   * Set a numerator of current time-signature.
   * @param timeSignatureNumerator
   */
  public void setTimeSignatureNumerator(int timeSignatureNumerator) {
    this.timeSignatureNumerator = timeSignatureNumerator;
  }

  /**
   * Returns a denominator of current time-signature.
   * @return
   */
  public int geTimeSignatureDenominator() {
    return timeSignatureDenominator;
  }

  /**
   * Set a denominator of current time-signature.
   * @param timeSignatureDenominator
   */
  public void setTimeSignatureDenominator(int timeSignatureDenominator) {
    this.timeSignatureDenominator = timeSignatureDenominator;
  }

  /**
   * Returns a ticks duration of current time-signature.
   * @return
   */
  public int geTimeSignatureTicks() {
    return timeSignatureTicks;
  }

  /**
   * Set a ticks duration of current time-signature.
   * @param timeSignatureTicks
   */
  public void setTimeSignatureTicks(int timeSignatureTicks) {
    this.timeSignatureTicks = timeSignatureTicks;
  }
}
