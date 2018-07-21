package com.github.jhorology.bitwig.extension;

import java.util.Map;
import java.util.HashMap;

import com.bitwig.extension.controller.api.ControllerHost;

import com.github.jhorology.bitwig.extension.AbstractExtension;

/**
 * A context holder for ExtentionThreadExecutor task state.
 * This class assumes that all methods are called from within "Control Surface Session' thread.
 */
public class ExecutionContext {
    private static ExecutionContext instance;

    private AbstractExtension extension;
    private final Map<String, Object> values;
    static void init(AbstractExtension extension) {
        instance = new ExecutionContext(extension);
    }

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
    
    private ExecutionContext(AbstractExtension extension) {
        this();
        this.extension = extension;
    }
    
    /**
     * get a Extension instance.
     * @return
     */
    public AbstractExtension getExtension() {
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
