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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A set of utility functions for Bitwig API.
 */
public class ExtensionUtils {

  /**
   * Populate JSON properties to fields of specified object instance.
   * The fields of instance should be annotated with {@link com.google.gson.annotations.Expose @Expose}.
   * @param <T>          the type of the specified object
   * @param resourceName the resource name of JSON.
   * @param instance     an object of type T.
   * @throws java.io.IOException
   */
  public static <T> void populateJsonProperties(
    String resourceName,
    T instance
  ) throws IOException {
    try (
      Reader reader = new InputStreamReader(
        instance.getClass().getClassLoader().getResourceAsStream(resourceName),
        StandardCharsets.UTF_8
      )
    ) {
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
    try (
      Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)
    ) {
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
      .registerTypeAdapter(
        instance.getClass(),
        (InstanceCreator<T>) t -> instance
      )
      .create();
    gson.fromJson(reader, instance.getClass());
  }

  /**
   * deep copy all properties that are annotated with @Expose.<br>
   * @param src
   * @param dst
   */
  public static <T> void deepCopy(T src, T dst) {
    Gson gson = new GsonBuilder()
      .excludeFieldsWithoutExposeAnnotation()
      .registerTypeAdapter(dst.getClass(), (InstanceCreator<T>) t -> dst)
      .create();
    String json = gson.toJson(src);
    gson.fromJson(json, dst.getClass());
  }

  /**
   * Write the JSON file of specified instance.<br>
   * The fields of instance should be annotated with {@link com.google.gson.annotations.Expose @Expose}.
   * @param instance      the object for which JSON representation is to be created.
   * @param file          to which the JSON file of instance needs to be written.
   * @throws IOException
   */
  public static void writeJsonFile(Object instance, Path file)
    throws IOException {
    Gson gson = new GsonBuilder()
      .excludeFieldsWithoutExposeAnnotation()
      .create();
    try (
      Writer writer = Files.newBufferedWriter(
        file,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      )
    ) {
      gson.toJson(instance, writer);
    }
  }
}
