/*
 * Copyright (c) 2020 Masafumi Fujimaru
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
package com.github.jhorology.bitwig.extension;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.github.jhorology.bitwig.logging.impl.LogSeverity;
import com.google.common.eventbus.Subscribe;
import com.google.gson.annotations.Expose;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A base class for managing extension's configuration.
 */
public abstract class AbstractConfiguration {

  // populate from json -->
  @Expose
  private LogSeverity logLevel = LogSeverity.WARN;

  //#if build.development
  @Expose
  //#endif
  private boolean logOutputSystemConsole = false;

  @Expose
  protected boolean doNotUseRequestFlush = false;

  // <--

  private ControllerHost host;
  private boolean ignoreHostPrefValue;
  private boolean valueChanged;
  private boolean requestReset;

  /**
   * Default constructor.
   */
  public AbstractConfiguration() {}

  /**
   * Return a log level that defined as configuration value.
   * @return
   */
  public LogSeverity getLogLevel() {
    return logLevel;
  }

  /**
   * Return a flag to output log to system console, or not.
   * @return
   */
  public boolean isLogOutputSystemConsole() {
    return logOutputSystemConsole;
  }

  /**
   * Return a flag to use Hos#requestFlush for thread dispatching, or not.
   * for performance test.
   * @return
   */
  public boolean isDoNotUseRequestFlush() {
    return doNotUseRequestFlush;
  }

  // TODO Guava 19 or above are able to register non-public @﻿Subscribe

  /**
   * this method is called at extension's start of life-cycle.
   * Do not call or override this method.
   * @param e
   */
  @Subscribe
  public final void onInit(InitEvent<?> e) {
    host = e.getHost();
    requestReset = false;
    valueChanged = false;
    Predicate<LogSeverity> logLevelEnumFilter = level -> true;
    //#if build.production
    logLevelEnumFilter = level -> level.ordinal() >= LogSeverity.INFO.ordinal();
    //#endif
    ignoreHostPrefValue();

    addEnumPrefItem(
      "Log Level",
      "Logging",
      logLevelEnumFilter,
      t -> t.name(),
      this::getLogLevel,
      v -> {
        logLevel = v;
      }
    );

    //#if build.development
    addBoolPrefItem(
      "Output system console",
      "Logging",
      this::isLogOutputSystemConsole,
      v -> {
        logOutputSystemConsole = v;
      }
    );
    //#endif

    addPrefItems();

    host
      .getPreferences()
      .getSignalSetting(
        "Apply new settings",
        "Restart this extension",
        "Restart"
      )
      .addSignalObserver(host::restart);

    host
      .getPreferences()
      .getSignalSetting(
        "Reset to defaults",
        "Restart this extension",
        "Restart"
      )
      .addSignalObserver(() -> {
        requestReset = true;
        host.restart();
      });
  }

  /**
   * Add input elements of preferences panel.
   */
  protected abstract void addPrefItems();

  // TODO Guava 19 or above EventBus is able to register non-public @﻿Subscribe

  /**
   * this method is called at extension's end of life-cycle.
   * Do not call this method.
   * @param e
   */
  @Subscribe
  public final void onExit(ExitEvent<?> e) {}

  /**
   * Returns configuration has bean changed, or not.
   * @return
   */
  boolean isValueChanged() {
    return valueChanged;
  }

  /**
   * Sets configuration has bean changed, or not.
   * @param valueChanged
   */
  void setValueChanged(boolean valueChanged) {
    this.valueChanged = valueChanged;
  }

  /**
   * Indicates whether the configuration is requested to reset.
   * @return
   */
  boolean isRequestReset() {
    return requestReset;
  }

  /**
   * Sets whether to reset this configuration.
   * @param requestRest
   */
  void setRequestReset(boolean requestReset) {
    this.requestReset = requestReset;
  }

  /**
   * Add a input item to preferences panel.
   * @param <T>           the enum type
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param getter
   * @param setter
   */
  protected <T extends Enum<T>> void addEnumPrefItem(
    String label,
    String category,
    Supplier<T> getter,
    Consumer<T> setter
  ) {
    addEnumPrefItem(label, category, T -> true, T -> T.name(), getter, setter);
  }

  /**
   * Add a input item to preferences panel.
   * @param <T>           the enum type
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
   * @param getter
   * @param setter
   */
  protected <T extends Enum<T>> void addEnumPrefItem(
    String label,
    String category,
    Function<T, String> mapper,
    Supplier<T> getter,
    Consumer<T> setter
  ) {
    addEnumPrefItem(label, category, T -> true, mapper, getter, setter);
  }

  /**
   * Add a input item to preferences panel.
   * @param <T>           the enum type.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param filter        the filter of enum elements.
   * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
   *                      return value should be unique.
   * @param getter
   * @param setter
   */
  protected <T extends Enum<T>> void addEnumPrefItem(
    String label,
    String category,
    Predicate<T> filter,
    Function<T, String> mapper,
    Supplier<T> getter,
    Consumer<T> setter
  ) {
    Class<T> enumClass = getter.get().getDeclaringClass();
    T[] values = enumClass.getEnumConstants();
    // host thrown exception
    // Enum settings should have least two options.
    if (values.length <= 1) {
      return;
    }

    // display string -> enum
    Function<String, T> valueOf =
      (
        s ->
          Stream
            .of(values)
            .filter(e -> mapper.apply(e).equals(s))
            .findFirst()
            .orElse(getter.get())
      );

    SettableEnumValue value = host
      .getPreferences()
      .getEnumSetting(
        label,
        category,
        Stream.of(values).filter(filter).map(mapper).toArray(String[]::new),
        mapper.apply(getter.get())
      );

    value.set(mapper.apply(getter.get()));
    value.addValueObserver((String s) -> {
      if (ignoreHostPrefValue) {
        value.set(mapper.apply(getter.get()));
      } else {
        T v = valueOf.apply(s);
        if (getter.get() != v) {
          setter.accept(v);
          valueChanged = true;
        }
      }
    });
  }

  /**
   * Add a input item to preferences panel.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param options       the array of value options.
   * @param getter
   * @param setter
   */
  protected void addIntPrefItem(
    String label,
    String category,
    int[] options,
    Supplier<Integer> getter,
    Consumer<Integer> setter
  ) {
    // host thrown exception
    // Enum settings should have least two options.
    if (options == null || options.length <= 1) {
      return;
    }
    Supplier<Integer> safeValue = () -> {
      int v = getter.get() == null ? options[0] : getter.get();
      for (int opt : options) {
        if (v <= opt) return opt;
      }
      return options[options.length - 1];
    };
    SettableEnumValue value = host
      .getPreferences()
      .getEnumSetting(
        label,
        category,
        IntStream.of(options).mapToObj(String::valueOf).toArray(String[]::new),
        String.valueOf(safeValue.get())
      );

    value.set(String.valueOf(safeValue.get()));
    value.addValueObserver((String s) -> {
      if (ignoreHostPrefValue) {
        value.set(String.valueOf(safeValue.get()));
      } else {
        int v = Integer.valueOf(s);
        if (safeValue.get() != v) {
          setter.accept(v);
          valueChanged = true;
        }
      }
    });
  }

  /**
   * Add a input item to preferences panel.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param minValue
   * @param maxValue
   * @param unit
   * @param getter
   * @param setter
   */
  protected void addIntPrefItem(
    String label,
    String category,
    int minValue,
    int maxValue,
    String unit,
    Supplier<Integer> getter,
    Consumer<Integer> setter
  ) {
    Supplier<Integer> safeValue = () -> {
      int v = getter.get() == null ? minValue : getter.get();
      if (v < minValue) v = minValue;
      if (v > maxValue) v = maxValue;
      return v;
    };
    SettableRangedValue value = host
      .getPreferences()
      .getNumberSetting(
        label,
        category,
        minValue,
        maxValue,
        1,
        unit,
        safeValue.get()
      );
    value.setRaw(safeValue.get());
    value.addRawValueObserver((double v) -> {
      if (ignoreHostPrefValue) {
        value.setRaw(safeValue.get());
      } else if (safeValue.get() != (int) v) {
        setter.accept((int) v);
        valueChanged = true;
      }
    });
  }

  /**
   * Add a input item to preferences panel.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param getter
   * @param setter
   */
  protected void addBoolPrefItem(
    String label,
    String category,
    Supplier<Boolean> getter,
    Consumer<Boolean> setter
  ) {
    addBoolPrefItem(label, category, getter, setter, true);
  }

  /**
   * Add a input item to preferences panel.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param getter
   * @param setter
   * @param useRcFile
   */
  protected void addBoolPrefItem(
    String label,
    String category,
    Supplier<Boolean> getter,
    Consumer<Boolean> setter,
    boolean useRcFile
  ) {
    SettableBooleanValue value = host
      .getPreferences()
      .getBooleanSetting(label, category, getter.get());
    if (useRcFile) {
      value.set(getter.get());
    }
    value.addValueObserver((boolean v) -> {
      if (useRcFile && ignoreHostPrefValue) {
        value.set(getter.get());
      } else if (getter.get() != v) {
        setter.accept(v);
        if (useRcFile) {
          valueChanged = true;
        }
      }
    });
  }

  /**
   * Add a input item to preferences panel.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param length
   * @param getter
   * @param setter
   */
  protected void addStringPrefItem(
    String label,
    String category,
    int length,
    Supplier<String> getter,
    Consumer<String> setter
  ) {
    addStringPrefItem(label, category, length, getter, setter, true);
  }

  /**
   * Add a input item to preferences panel.
   * @param label         the name of the setting, must not be null
   * @param category      the name of the category, may not be null
   * @param length
   * @param getter
   * @param setter
   * @param useRcFile
   */
  protected void addStringPrefItem(
    String label,
    String category,
    int length,
    Supplier<String> getter,
    Consumer<String> setter,
    boolean useRcFile
  ) {
    SettableStringValue value = host
      .getPreferences()
      .getStringSetting(label, category, length, getter.get());

    if (useRcFile) {
      value.set(getter.get());
    }
    value.addValueObserver((String v) -> {
      if (useRcFile && ignoreHostPrefValue) {
        value.set(getter.get());
      } else if (!getter.get().equals(v)) {
        setter.accept(v);
        if (useRcFile) {
          valueChanged = true;
        }
      }
    });
  }

  private void ignoreHostPrefValue() {
    ignoreHostPrefValue = true;
    host.scheduleTask(
      () -> {
        ignoreHostPrefValue = false;
      },
      500L
    );
  }
}
