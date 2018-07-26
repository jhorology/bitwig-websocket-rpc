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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bitwig.extension.callback.StringValueChangedCallback;
import com.bitwig.extension.controller.api.Value;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;
import com.github.jhorology.bitwig.websocket.protocol.RequestContext;

/**
 * An imnplementation of the core RPC methods.
 */
public class RpcImpl implements Rpc {
    /**
     * Add the remote connection to subscriber list of each event.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     * @see https://github.com/elpheria/rpc-websockets
     */
    @Override
    public Map<String, String> on(String... eventNames) {
        return acceptEvents(eventNames, (e, c) -> e.subscribe(c));
    }

    /**
     * Add the remote connection to subscriber list of each event.
     * The next time event is triggered, this subscriber is removed and then invoked.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    @Override
    public Map<String, String> once(String... eventNames) {
        return acceptEvents(eventNames, (e, c) -> e.subscribeOnce(c));
    }

    /**
     * Remove the remote connection from subscriber list of each event.
     * @param eventNames the names of event to unsubscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    @Override
    public Map<String, String> off(String... eventNames) {
        return acceptEvents(eventNames, (e, c) -> e.unsubscribe(c));
    }

    /**
     * wait for next tick to gurantee value integrity of concurrency.
     */
    @Override
    public void nextTick() {
        nextTick(0);
    }
    
    /**
     * wait for next tick to gurantee value integrity of concurrency.
     * @param millis
     */
    @Override
    public void nextTick(long millis) {
        RequestContext.getContext().nextTick(millis);
    }
    
    /**
     * log event for debugging
     * @return
     */
    @Override
    public Value<StringValueChangedCallback> log() {
        // TODO
        return null;
    }
    
    /**
     * just return back message to remote connection.
     * @param message
     * @return message
     */
    @Override
    public String echo(String message) {
        return message;
    }
    
    /**
     * broadcast message to all remote connections.
     * @param message
     */
    @Override
    public void broadcast(String message, Object[] params) {
    }

    /**
     * report all RPC methods and events.
     * @return report
     */
    @Override
    public Object report() {
        RequestContext context = RequestContext.getContext();
        RpcRegistry registry = context.getRpcRegistry();
        return registry.report();
    }

    private Map<String, String> acceptEvents(String[] eventNames, BiConsumer<RpcEvent, WebSocket> lambda) {
        return Stream.of(eventNames)
            .map(s -> acceptEvent(s, lambda))
            .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }
    
    private ImmutablePair<String, String> acceptEvent(String eventName, BiConsumer<RpcEvent, WebSocket> lambda) {
        RequestContext context = RequestContext.getContext();
        RpcRegistry registry = context.getRpcRegistry();
        WebSocket client = context.getConnection();
        RpcEvent event = registry.getRpcEvent(eventName);
        if (event == null) {
            return new ImmutablePair<>(eventName, ERROR_EVENT_NOT_FOUND);
        }
        try {
            lambda.accept(event, client);
            return new ImmutablePair<>(eventName, OK);
        } catch (RpcException ex) {
            Logger.getLogger(RpcImpl.class).error(ex);
            return new ImmutablePair<>(eventName, ERROR_INTERNAL_ERROR);
        }
    }
}
