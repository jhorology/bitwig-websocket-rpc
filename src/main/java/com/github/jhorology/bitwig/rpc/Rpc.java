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
package com.github.jhorology.bitwig.rpc;

// jdk
import java.util.Map;

// bitwig api
import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.bitwig.extension.callback.StringValueChangedCallback;
import com.bitwig.extension.controller.api.Value;

/**
 * An interface for core RPC methods.
 */
public interface Rpc {
    public static final String OK = "ok";
    public static final String ERROR_EVENT_NOT_FOUND = "Event not found";
    public static final String ERROR_INTERNAL_ERROR = "Internal Error";
    
    /**
     * Add the remote connection to subscriber list of each event.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     * @see https://github.com/elpheria/rpc-websockets
     */
    Map<String, String> on(String... eventNames);

    /**
     * Remove the remote connection from subscriber list of each event.
     * @param eventNames the names of event to unsubscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    Map<String, String> off(String... eventNames);
    
    /**
     * just return back message to remote connection.
     * @param message
     * @return message
     */
    String echo(String message);

    /**
     * broadcast message to all remote connections.
     * @param message
     * @param params named parameters
     */
    void broadcast(String message, Object params);
    
    /**
     * report all RPC methods and events.
     * @return report
     */
    Object report();

    /**
     * log event for debugging
     * @return
     */
    Value<StringValueChangedCallback> log();
}
