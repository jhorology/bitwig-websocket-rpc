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
package com.github.jhorology.bitwig.websocket.protocol;

import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.extension.ExecutionContext;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;

/**
 * A context holder for RPC request state. <br>
 * This class is intnded to pass through the values to RPC invocation method.
 */
public class RequestContext {
    private WebSocket connection;
    private ReflectionRegistry registry;

    // private constructor to prevent instantiation of this class
    private RequestContext() {
    }
    
    // private constructor to prevent instantiation of this class
    private RequestContext(WebSocket connection, ReflectionRegistry registry) {
        this.connection = connection;
        this.registry = registry;
    }
    
    /**
     * Initialize the context.
     * @param connection
     * @param registry
     */
    static void init(WebSocket connection, ReflectionRegistry registry) {
        RequestContext context = new RequestContext(connection, registry);
        ExecutionContext.getContext().set(RequestContext.class, context);
    }

    /**
     * Get a current context.
     * @return The current context.
     */
    public static RequestContext getContext() {
        return ExecutionContext.getContext().get(RequestContext.class);
    }

    /**
     * Get a client connection that requested current operation.
     * @return The current context.
     */
    public WebSocket getConnection() {
        return connection;
    }

    /**
     * Get a instance of ReflectionRegistry.
     * @return The current context.
     */
    public ReflectionRegistry getReflectionRegistry() {
        return registry;
    }
}
