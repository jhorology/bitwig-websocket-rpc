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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;

// dependencies
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import java.util.Arrays;

/**
 * A set of utility functions for Bitwig API.
 */
public class ExtensionUtils {
    /**
     * Get a preference value as enum value.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            T initialValue,
                                                            T settingValue) {
        return getPreferenceAsEnum(host, label, category, null, initialValue, settingValue, null);
    }
    
    /**
     * Get a preference value as enum value.
     * @param <T>           the enum type to be returned.
     * @param preferences   the interface of Preferences
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(Preferences preferences,
                                                            String label, String category,
                                                            T initialValue,
                                                            T settingValue) {
        return getPreferenceAsEnum(preferences, label, category, null, initialValue, settingValue, null);
    }
    
    /**
     * Get a preference value as enum value.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            Function<T, String> mapper,
                                                            T initialValue,
                                                            T settingValue) {
        return getPreferenceAsEnum(host, label, category, mapper, initialValue, settingValue, null);
    }
    
    /**
     * Get a preference value as enum value.
     * @param <T>           the enum type to be returned.
     * @param preferences   the interface of Preferences
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(Preferences preferences,
                                                            String label, String category,
                                                            Function<T, String> mapper,
                                                            T initialValue,
                                                            T settingValue) {
        return getPreferenceAsEnum(preferences, label, category, mapper, initialValue, settingValue, null);
    }

    /**
     * Get a prefrence value as enum value.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            T initialValue,
                                                            T settingValue,
                                                            BiConsumer<T, SettableEnumValue> onChange) {
        return getPreferenceAsEnum(host, label, category, null, initialValue, settingValue, onChange);
    }
    
    /**
     * Get a prefrence value as enum value.
     * @param <T>           the enum type to be returned.
     * @param preferences   the interface of Preferences
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(Preferences preferences,
                                                            String label, String category,
                                                            T initialValue,
                                                            T settingValue,
                                                            BiConsumer<T, SettableEnumValue> onChange) {
        return getPreferenceAsEnum(preferences, label, category, null, initialValue, settingValue, onChange);
    }

    /**
     * Get a preference value as enum.
     * @param <T>           the enum type to be returned.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param settingValue
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(ControllerHost host,
                                                            String label, String category,
                                                            Function<T, String> mapper,
                                                            T initialValue,
                                                            T settingValue,
                                                            BiConsumer<T, SettableEnumValue> onChange) {
        return getPreferenceAsEnum
            (host.getPreferences(), label, category, mapper, initialValue, settingValue, onChange);
    }
    
    /**
     * Get a preference value as enum.
     * @param <T>           the enum type to be returned.
     * @param preferences   the interface of Preferences
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param mapper        the mapper function to convert enum value to string value to be displayed on preference panel.
     *                      return value should be unique.
     * @param settingValue
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static <T extends Enum<T>> T getPreferenceAsEnum(Preferences preferences,
                                                            String label, String category,
                                                            Function<T, String> mapper,
                                                            T initialValue,
                                                            T settingValue,
                                                            BiConsumer<T, SettableEnumValue> onChange) {
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
        String strInitialValue = mapper == null
            ? initialValue.name()
            : mapper.apply(initialValue);

        Function<String, T> valueOf = mapper == null
            ? (s -> T.valueOf(enumClass, s))
            : (s -> Stream.of(values)
               .filter(e -> mapper.apply(e).equals(s))
               .findFirst().orElse(initialValue));

        SettableEnumValue value =
            preferences.getEnumSetting(label, category, strValues, strInitialValue);
        if (settingValue != null) {
            value.set(settingValue.name());
        }
        if (onChange != null) {
            value.addValueObserver((String s) -> onChange.accept(valueOf.apply(s), value));
        }
        if (settingValue != null) {
            return settingValue;
        }
        return valueOf.apply(value.get());
    }

    /**
     * Get a preference value as int that is selected from options.
     * @param host          the interface of ControllerHost
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @param options       the array of value options.
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static int getPreferenceAsIntOptions(ControllerHost host,
                                                String label, String category,
                                                int initialValue,
                                                int settingValue,
                                                int[] options,
                                                BiConsumer<Integer,SettableEnumValue> onChange) {
        return getPreferenceAsIntOptions
            (host.getPreferences(), label, category, initialValue, settingValue, options, onChange);
    }
    
    /**
     * Get a preference value as int that is selected from options.
     * @param preferences   the interface of Preferences
     * @param label         the name of the setting, must not be null
     * @param category      the name of the category, may not be null
     * @param initialValue  the initial string value, must be one of the items specified with the option argument
     * @param settingValue
     * @param options       the array of value options.
     * @param onChange      the lamda consumer to be called on value has changed
     * @return the preference value
     */
    public static int getPreferenceAsIntOptions(Preferences preferences,
                                                String label, String category,
                                                int initialValue,
                                                int settingValue,
                                                int[] options,
                                                BiConsumer<Integer, SettableEnumValue> onChange) {
        // host thrown exception
        // Enum settings should have at least two options.
        if (options == null || options.length <= 1) {
            return initialValue;
        }

        String[] strOptions = Arrays.stream(options)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);
        String strInitialValue = String.valueOf(initialValue);
        SettableEnumValue value =
            preferences.getEnumSetting(label, category, strOptions, strInitialValue);
        value.set(String.valueOf(settingValue));
        if (onChange != null) {
            value.addValueObserver((String s) -> onChange.accept(Integer.valueOf(s), value));
        }
        return settingValue;
    }
    
    /**
     * Populate JSON properties to fields of specified object instance.
     * The fields of instance should be annotated with {@link com.google.gson.annotations.Expose @Expose}.
     * @param <T>          the type of the specified object
     * @param resourceName the resource name of JSON.
     * @param instance     an object of type T.
     * @throws java.io.IOException
     */
    public static <T> void populateJsonProperties(String resourceName, T instance)
        throws IOException {
        try (Reader reader =
             new InputStreamReader(instance.getClass()
                                   .getClassLoader()
                                   .getResourceAsStream(resourceName),
                                   "UTF-8")) {
            populateJsonProperties(reader, instance);
        }
    }

    /**
     * Populate JSON properties to fields of specified object instance.
     * The fields of instance should be annotated with {@link com.google.gson.annotations.Expose @Expose}.
     * @param <T>      the type of the specified object
     * @param file     the file path of JSON.
     * @param instance an object of type T.
     * @throws java.io.IOException
     */
    public static <T> void populateJsonProperties(Path file, T instance)
        throws IOException {
        if (!Files.isReadable(file)) return;
        try (Reader reader =
             Files.newBufferedReader(file,
                                     Charset.forName("UTF-8"))) {
            populateJsonProperties(reader, instance);
        }
    }

    /**
     * Populate JSON properties to fields of specified object instance.
     * The fields of instance should be annotated with {@link com.google.gson.annotations.Expose @Expose}.
     * @param <T>      the type of the specified object
     * @param reader   the reader producing the JSON.
     * @param instance an object of type T.
     */
    public static <T> void populateJsonProperties(Reader reader, T instance) {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(instance.getClass(),
                                 (InstanceCreator<T>)t -> instance)
            .create();
        gson.fromJson(reader, instance.getClass());
    }

    /**
     * Write the JSON file of specified instance.<br>
     * The fields of instance should be annotated with {@link com.google.gson.annotations.Expose @Expose}.
     * @param instance      the object for which JSON representation is to be created.
     * @param file          to which the JSON file of instance needs to be written.
     * @throws IOException
     */
    public static void writeJsonFile(Object instance, Path file) throws IOException {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
        try (Writer writer =
             Files.newBufferedWriter(file,
                                     Charset.forName("UTF-8"),
                                     StandardOpenOption.CREATE,
                                     StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(instance, writer);
        }
    }
}
