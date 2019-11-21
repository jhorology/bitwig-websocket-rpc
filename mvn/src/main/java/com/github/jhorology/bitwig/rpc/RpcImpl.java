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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.StringValue;

// provided dependencies
import org.apache.commons.lang3.StringUtils;

// dependencies
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.WebSocketRpcServerExtensionDefinition;
import com.github.jhorology.bitwig.extension.ExecutionContext;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;
import com.github.jhorology.bitwig.websocket.protocol.RequestContext;

/**
 * An implementation of the core RPC methods.
 */
public class RpcImpl implements Rpc {
    private static final Logger LOG = LoggerFactory.getLogger(RpcImpl.class);
    
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
     * Remove the remote connection from subscriber list of each event.
     * @param eventNames the names of event to unsubscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    @Override
    public Map<String, String> off(String... eventNames) {
        return acceptEvents(eventNames, (e, c) -> e.unsubscribe(c));
    }

    /**
     * log event for debugging
     * @return
     */
    @Override
    public StringValue log() {
        Logger log = LoggerFactory.getLogger("rpc.log");
        log.info("Hello!");
        return (StringValue)log;
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
    public void broadcast(String message, Object params) {
        RequestContext context = RequestContext.getContext();
        PushModel pushModel = context.getPushModel();
        if (pushModel != null) {
            pushModel.broadcast(new Notification(message, params));
        }
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

    /**
     * remote configuration
     * @param config
     */
    @Override
    public void config(Config config) {
        ExecutionContext.getContext()
            .getExtension().setConfig(config);
    }

    /**
     * Return a current configuration
     * @return
     */
    @Override
    public Config config() {
        return ((Config)ExecutionContext.getContext().getConfig());
    }
    
    private Map<String, String> acceptEvents(String[] eventNames, BiConsumer<RpcEvent, WebSocket> lambda) {
        return Stream.of(eventNames)
            .map(s -> acceptEvent(s, lambda))
            .collect(Collectors.toMap(r -> r[0], r -> r[1]));
    }
    
    private String[] acceptEvent(String eventName, BiConsumer<RpcEvent, WebSocket> lambda) {
        RequestContext context = RequestContext.getContext();
        RpcRegistry registry = context.getRpcRegistry();
        WebSocket client = context.getConnection();
        RpcEvent event = registry.getRpcEvent(eventName);
        if (event == null) {
            return new String[] {eventName, ERROR_EVENT_NOT_FOUND};
        }
        try {
            lambda.accept(event, client);
            return new String[] {eventName, OK};
        } catch (RpcException ex) {
            LOG.error("Error on acceptEvents()", ex);
            return new String [] {eventName, ex.getMessage()};
        } catch (Throwable ex) {
            LOG.error("Error on acceptEvents()", ex);
            return new String [] {eventName, error(ex, ERROR_INTERNAL_ERROR)};
        }
    }

    private static String error(Throwable ex, String defaultMessage) {
        String errorMessage = ex.getMessage();
        if (StringUtils.isEmpty(errorMessage)) {
            errorMessage = defaultMessage;
        }
        return errorMessage;
    }
}
