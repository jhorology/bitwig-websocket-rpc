package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;

public class StringMessageEvent extends AbstractWebSocketEvent {
    private final String message;
    
    StringMessageEvent(WebSocket conn, String message) {
        super(conn);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
