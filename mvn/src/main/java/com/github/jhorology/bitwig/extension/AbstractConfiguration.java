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
package com.github.jhorology.bitwig.extension;

// jdk
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;

// provided dependencies
import com.google.gson.annotations.Expose;

// source
import org.slf4j.impl.LogSeverity;

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
    protected boolean logOutputSystemConsole = false;
    // <--

    protected ControllerHost host;
    private boolean ignoreHostPrefValue;
    protected boolean valueChanged;
    private boolean requestReset;

    /**
     * Default constructor.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AbstractConfiguration() {
    }

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

    protected void onInit(InitEvent<?> e) {
        host = e.getHost();
        requestReset = false;
        valueChanged = false;
        
        ignoreHostPrefValue();

        addEnumPrefItem("Log Level", "Logging",
                        this::getLogLevel,
                        v -> {logLevel = v;});

        //#if build.development
        addBoolPrefItem("Output system console", "Logging",
                        this::isLogOutputSystemConsole,
                        v -> {logOutputSystemConsole = v;});
        //#endif

        insertPrefItems();
        
        host.getPreferences()
            .getSignalSetting("Apply new settings", "Restart (new settings need restart)", "Restart")
            .addSignalObserver(host::restart);

        host.getPreferences()
            .getSignalSetting("Reset to defaults", "Restart (new settings need restart)", "Restart")
            .addSignalObserver(() -> {
                    requestReset = true;;
                    host.restart();
                });
    }
    
    /**
     * insert input element of preferences panel.
     */
    abstract protected void insertPrefItems();

    protected void onExit(ExitEvent<?> e) {
    }
    
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
     * Returns configuration has bean requested to reset, or not.
     * @return
     */
    boolean isRequestReset() {
        return requestReset;
    }

    /**
     * Add a input item to prefrences panel.
     * @param <T>           the enum type
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param getter
     * @param setter
     */
    protected <T extends Enum<T>> void addEnumPrefItem(String label,
                                                       String category,
                                                       Supplier<T> getter,
                                                       Consumer<T> setter) {
        addEnumPrefItem(label, category, null, getter, setter);
    }


    /**
     * Add a input item to prefrences panel.
     * @param <T>           the enum type.
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param getter
     * @param setter
     */
    protected <T extends Enum<T>> void addEnumPrefItem(String label,
                                                       String category,
                                                       Function<T, String> mapper,
                                                       Supplier<T> getter,
                                                       Consumer<T> setter) {
        Class<T> enumClass = getter.get().getDeclaringClass();
        T[] values = enumClass.getEnumConstants();
        // host thrown exception
        // Enum settings should have least two options.
        if (values.length <= 1) {
            return;
        }

        // enum -> display string
        Function<T, String> toStr = mapper != null ? mapper : (e -> e.name());

        // display string -> enum
        Function<String, T> valueOf = mapper == null
            ? (s -> T.valueOf(enumClass, s))
            : (s -> Stream.of(values)
               .filter(e -> mapper.apply(e).equals(s))
               .findFirst().orElse(getter.get()));

        SettableEnumValue value = host.getPreferences().getEnumSetting
            (label, category,
             Stream.of(values).map(toStr).toArray(String[]::new),
             toStr.apply(getter.get()));

        value.set(toStr.apply(getter.get()));
        value.addValueObserver((String s) -> {
                if (ignoreHostPrefValue) {
                    value.set(toStr.apply(getter.get()));
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
     * Add a input item to prefrences panel.
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param options       the array of value options.
     * @param getter
     * @param setter
     */
    protected void addIntPrefItem(String label,
                                  String category,
                                  int[] options,
                                  Supplier<Integer> getter,
                                  Consumer<Integer> setter) {
        // host thrown exception
        // Enum settings should have least two options.
        if (options == null || options.length <= 1) {
            return;
        }
        SettableEnumValue value = host.getPreferences().getEnumSetting
            (label, category,
             IntStream.of(options).mapToObj(String::valueOf).toArray(String[]::new),
             String.valueOf(getter.get()));

        value.set(String.valueOf(getter.get()));
        value.addValueObserver((String s) -> {
                if (ignoreHostPrefValue) {
                    value.set(String.valueOf(getter.get()));
                } else {
                    int v = Integer.valueOf(s);
                    if (getter.get() != v) {
                        setter.accept(v);
                        valueChanged = true;
                    }
                }
            });
    }

    /**
     * Add a input item to prefrences panel.
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param minValue
     * @param maxValue
     * @param unit
     * @param getter
     * @param setter
     */
    protected void addIntPrefItem(String label,
                                  String category,
                                  int minValue,
                                  int maxValue,
                                  String unit,
                                  Supplier<Integer> getter,
                                  Consumer<Integer> setter) {

        SettableRangedValue value =
            host.getPreferences().getNumberSetting
            (label, category, (double)minValue, (double)maxValue, 1, unit, (double)getter.get());

        value.setRaw(getter.get());
        value.addValueObserver((double v) -> {
                if (ignoreHostPrefValue) {
                    value.setRaw(getter.get());
                } else if (getter.get() != (int)v){
                    setter.accept((int)v);
                    valueChanged = true;
                }
            });
    }

    /**
     * Add a input item to prefrences panel.
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param getter
     * @param setter
     */
    protected void addBoolPrefItem(String label,
                                   String category,
                                   Supplier<Boolean> getter,
                                   Consumer<Boolean> setter) {

        SettableBooleanValue value =
            host.getPreferences().getBooleanSetting
            (label, category, getter.get());

        value.set(getter.get());
        value.addValueObserver((boolean v) -> {
                if (ignoreHostPrefValue) {
                    value.set(getter.get());
                } else if (getter.get() != v){
                    setter.accept(v);
                    valueChanged = true;
                }
            });
    }
    
    private void ignoreHostPrefValue() {
        ignoreHostPrefValue = true;
        host.scheduleTask(() -> {
                ignoreHostPrefValue = false;
            },
            500L);
    }
}
