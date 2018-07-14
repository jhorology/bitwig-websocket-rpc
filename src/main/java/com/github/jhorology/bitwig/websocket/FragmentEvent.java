package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;

public class FragmentEvent extends AbstractWebSocketEvent {
    private final Framedata fragment;
    
    FragmentEvent(WebSocket conn, Framedata fragment) {
        super(conn);
        this.fragment = fragment;
    }

    public Framedata getFragment() {
        return fragment;
    }
}
