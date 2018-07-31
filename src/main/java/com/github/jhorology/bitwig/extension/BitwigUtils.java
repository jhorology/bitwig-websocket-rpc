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

// jvm
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

// bitwig api
import com.bitwig.extension.callback.EnumValueChangedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;

/**
 * A set of utility functions for Bitwig API.
 */
public class BitwigUtils {
    /**
     * Get a prefrence value as enum.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            T initialValue) {
        return getPreferenceAsEnum(host, label, category, null, initialValue, null);
    }
    /**
     * Get a prefrence value as enum.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            Function<T, String> mapper,
                                                            T initialValue) {
        return getPreferenceAsEnum(host, label, category, mapper, initialValue, null);
    }
    
    /**
     * Get a prefrence value as enum.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            T initialValue,
                                                            Consumer<T> onChange) {
        return getPreferenceAsEnum(host, label, category, null, initialValue, onChange);
    }
    
    /**
     * Get a prefrence value as enum.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            Function<T, String> mapper,
                                                            T initialValue,
                                                            Consumer<T> onChange) {
        Preferences pref = host.getPreferences();
        Class<T> enumClass = initialValue.getDeclaringClass();
        T[] values = enumClass.getEnumConstants();

        // host thrown exception
        // Enum settings should have at least two options.
        if (values.length <= 1) {
            return initialValue;
        }
        
        String[] strValues = Stream.of(values)
            .map(e -> mapper == null ? e.name() : mapper.apply(e))
            .toArray(s -> new String[s]);
        
        SettableEnumValue value =
            pref.getEnumSetting(label, category, strValues, initialValue.name());
        if (onChange != null) {
            EnumValueChangedCallback callback = mapper == null
                ? (String v) -> onChange.accept(T.valueOf(enumClass, v))
                : (String v) -> {
                T enumValue = Stream.of(values)
                .filter(e -> mapper.apply(e).equals(v))
                .findFirst().orElse(initialValue);
                onChange.accept(enumValue);
            };
            value.addValueObserver(callback);
        }
        String strValue = value.get();
        return T.valueOf(enumClass, strValue);
    }
}
