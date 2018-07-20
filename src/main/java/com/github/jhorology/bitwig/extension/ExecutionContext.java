package com.github.jhorology.bitwig.extension;

import java.util.Map;
import java.util.HashMap;

import com.bitwig.extension.controller.api.ControllerHost;

/**
 * A context holder for ExtentionThreadExecutor task state.
 * This class assumes that all methods are called from within "Controller Surface Session' thread.
 */
public class ExecutionContext {
    private static ExecutionContext instance;

    private ControllerHost host;
    private final Map<String, Object> values;
        
    static void init(ControllerHost host) {
        instance = new ExecutionContext(host);
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
    
    private ExecutionContext(ControllerHost host) {
        this();
        this.host = host;
    }
    
    /**
     * get a ControllerHost interface.
     * @return
     */
    public ControllerHost getHost() {
        return host;
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
}
