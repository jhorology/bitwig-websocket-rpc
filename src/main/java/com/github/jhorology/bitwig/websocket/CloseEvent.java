package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class CloseEvent extends AbstractWebSocketEvent {
    private final int code;
    private final String reason;
    private final boolean remote;
    
    CloseEvent(WebSocket conn, int code, String reason, boolean remote) {
        super(conn);
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
    
    public boolean isRemote() {
        return remote;
    }
}
