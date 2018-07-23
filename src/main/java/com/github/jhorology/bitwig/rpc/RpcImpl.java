package com.github.jhorology.bitwig.rpc;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.reflect.ReflectionRegistry;
import com.github.jhorology.bitwig.websocket.protocol.RequestContext;

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
    
    private Map<String, String> acceptEvents(String[] eventNames, BiConsumer<RpcEvent, WebSocket> lamda) {
        return Stream.of(eventNames)
            .map(s -> acceptEvent(s, lamda))
            .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }
    
    private RpcEvent getEvent(String eventName) {
        RequestContext context = RequestContext.getContext();
        ReflectionRegistry registry = context.getReflectionRegistry();
        RpcEvent event = registry.getEvent(eventName);
        return event;
    }
    
    private ImmutablePair<String, String> acceptEvent(String eventName, BiConsumer<RpcEvent, WebSocket> lamda) {
        RpcEvent event = getEvent(eventName);
        if (event == null) {
            return new ImmutablePair<>(eventName, ERROR_EVENT_NOT_FOUND);
        }
        WebSocket client = RequestContext.getContext()
            .getConnection();
        lamda.accept(event, client);
        return new ImmutablePair<>(eventName, OK);
    }
}
