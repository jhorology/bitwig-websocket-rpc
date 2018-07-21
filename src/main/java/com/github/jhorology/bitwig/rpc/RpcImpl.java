package com.github.jhorology.bitwig.rpc;

import java.util.Map;

public class RpcImpl implements Rpc {
    /**
     * Add the remote connection to subscriber list of each event.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     * @see https://github.com/elpheria/rpc-websockets
     */
    @Override
    public Map<String, String> on(String... eventNames) {
        return null;
    }

    /**
     * Add the remote connection to subscriber list of each event.
     * The next time event is triggered, this subscriber is removed and then invoked.
     * @param eventNames the names of event to subscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    @Override
    public Map<String, String> once(String... eventNames) {
        return null;
    }
    
    /**
     * Remove the remote connection from subscriber list of each event.
     * @param eventNames the names of event to unsubscribe.
     * @return the mapped results of each event. "ok" or error message.
     */
    @Override
    public Map<String, String> off(String... eventNames) {
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
    public void chat(String message) {
    }
}
