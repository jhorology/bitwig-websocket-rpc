package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;

public abstract class AbstractWebSocketEvent {
    protected final WebSocket conn;

    protected AbstractWebSocketEvent(WebSocket conn) {
        this.conn = conn;
    }

    public WebSocket getConnection() {
        return conn;
    }
}
