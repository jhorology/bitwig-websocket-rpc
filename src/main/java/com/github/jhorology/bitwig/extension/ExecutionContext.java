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
import java.util.Map;
import java.util.HashMap;

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;

/**
 * A context holder for executor task state. <br>
 * This class assumes that all methods are called from within "Control Surface Session" thread.
 */
public class ExecutionContext {
    private static final Logger LOG = Logger.getLogger(ExecutionContext.class);
    private static ExecutionContext instance;
    private AbstractExtension<? extends AbstractConfiguration> extension;
    private final Map<String, Object> values;
    private boolean nextTick;
    private long nextTickMillis;
    
    /**
     * initialize the context
     */
    static void init(AbstractExtension<? extends AbstractConfiguration> extension) {
        // for debug
        // checking re-entrant context
        if (instance != null) {
            RuntimeException ex = new RuntimeException("re-entrant context.");
            LOG.error(ex);
            throw ex;
        }
        instance = new ExecutionContext(extension);
    }

    /**
     * destroy the context
     */
    static void destroy() {
        instance.values.clear();
        instance = null;
    }
    
    /**
     * get a current context.
     * @return
     */
    public static ExecutionContext getContext() {
        return instance;
    }

    private ExecutionContext() {
        values = new HashMap<>();
    }
    
    private ExecutionContext(AbstractExtension<? extends AbstractConfiguration> extension) {
        this();
        this.extension = extension;
    }
    
    /**
     * get a Extension instance.
     * @return
     */
    public AbstractExtension<? extends AbstractConfiguration> getExtension() {
        return extension;
    }
    
    /**
     * get a ControllerHost interface.
     * @return 
     */
    public ControllerHost getHost() {
        return extension.getHost();
    }
    
    /**
     * gurantee to execute next command in next tick.
     * @param millis
     */
    public void nextTick(long millis) {
        nextTick = true;
        nextTickMillis = millis;
    }
    
    /**
     * nextTick() method was called in this context or not.
     * @return
     */
    public boolean isNextTickRequested() {
        return nextTick;
    }
    
    /**
     * nextTick() method was called in this context or not.
     * @return
     */
    public long getNextTickMillis() {
        return nextTickMillis;
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
     * @param clazz
     * @param value
     */
    public <T> void set(Class<T> clazz, T value) {
        values.put(clazz.getName(), value);
    }

    /**
     * get a contextual value by class.
     * @param clazz
     * @return value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T)values.get(clazz.getName());
    }
}
