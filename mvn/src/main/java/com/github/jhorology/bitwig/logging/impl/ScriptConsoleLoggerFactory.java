/*
 * This source code is based on https://github.com/qos-ch/slf4j/blob/v_1.7.25/slf4j-simple/src/main/java/org/slf4j/impl/SimpleLoggerFactory.java
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

// jdk
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
// dependencies
import org.slf4j.Logger;

/**
 * An implementation of {@link ILoggerFactory} which always returns
 * {@link SimpleLogger} instances.
 */
public class ScriptConsoleLoggerFactory implements ILoggerFactory {

  ConcurrentMap<String, Logger> loggerMap;

  public ScriptConsoleLoggerFactory() {
    loggerMap = new ConcurrentHashMap<>();
    ScriptConsoleLogger.lazyInit();
  }

  /**
   * Return an appropriate {@link SimpleLogger} instance by name.
   * @param name
   * @return
   */
  @Override
  public Logger getLogger(String name) {
    Logger simpleLogger = loggerMap.get(name);
    if (simpleLogger != null) {
      return simpleLogger;
    } else {
      Logger newInstance = new ScriptConsoleLogger(name);
      Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
      return oldInstance == null ? newInstance : oldInstance;
    }
  }

  /**
   * Clear the internal logger cache.
   *
   * This method is intended to be called by classes (in the same package) for
   * testing purposes. This method is internal. It can be modified, renamed or
   * removed at any time without notice.
   *
   * You are strongly discouraged from calling this method in production code.
   */
  void reset() {
    loggerMap.clear();
  }
}
