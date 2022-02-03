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

import com.github.jhorology.bitwig.logging.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * A context holder for executor task state.<br>
 This class assumes that all methods are called from within "Control Surface Session" thread.
 * @param <T> the type of configuration.
 */
public class ExecutionContext<T extends AbstractConfiguration>
  extends ExtensionContextBase<T> {

  private static final Logger LOG = LoggerFactory.getLogger(
    ExecutionContext.class
  );
  private static ExecutionContext<?> instance;
  private final Map<String, Object> values;
  private boolean initialized;

  /**
   * Constructor.
   * @param extension
   */
  ExecutionContext(AbstractExtension<T> extension) {
    super(extension);
    values = new HashMap<>();
    instance = this;
  }

  /**
   * get a current context.
   * @return
   */
  public static ExecutionContext<?> getContext() {
    return instance;
  }

  /**
   * initialize the context
   * @exception IllegalStateException throws when reentrant context.
   */
  void init() {
    // for debug
    // checking re-entrant context
    if (initialized) {
      IllegalStateException ex = new IllegalStateException(
        "re-entrant context."
      );
      LOG.error("Error re-entrant context.");
      throw ex;
    }
    initialized = true;
  }

  /**
   * destroy the context
   */
  void destroy() {
    instance.values.clear();
    initialized = false;
  }

  /**
   * set a contextual value with name.
   * @param name
   * @param value
   */
  public void set(String name, Object value) {
    values.put(name, value);
  }

  /**
   * get a contextual value by name.
   * @param name
   * @return value
   */
  public Object get(String name) {
    return values.get(name);
  }

  /**
   * set a contextual value with class.
   * @param <C>
   * @param clazz
   * @param value
   */
  public <C> void set(Class<C> clazz, C value) {
    values.put(clazz.getName(), value);
  }

  /**
   * get a contextual value by class.
   * @param <C>
   * @param clazz
   * @return value
   */
  @SuppressWarnings("unchecked")
  public <C> C get(Class<C> clazz) {
    return (C) values.get(clazz.getName());
  }
}
