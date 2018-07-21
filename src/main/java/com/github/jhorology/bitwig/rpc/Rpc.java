package com.github.jhorology.bitwig.rpc;

import java.util.Map;

/**
 * An interface to support handling events.
 * @see https://github.com/elpheria/rpc-websockets
 */
public interface Rpc {
    public static final String OK = "ok";
    public static final String ERROR_EVENT_NOT_FOUND = "Event not found";
    
    /**
     * Add the remote connection to subscriber list of each event.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     * @see https://github.com/elpheria/rpc-websockets
     */
    Map<String, String> on(String... eventNames);

    /**
     * Add the remote connection to subscriber list of each event.
     * The next time event is triggered, this subscriber is removed and then invoked.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    Map<String, String> once(String... eventNames);
    
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
     */
    void broadcast(String message, Object[] params);
}
