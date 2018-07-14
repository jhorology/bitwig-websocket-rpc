package com.github.jhorology.bitwig.websocket;

import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;

public class BufferMessageEvent extends AbstractWebSocketEvent {
    private final ByteBuffer message;
    
    BufferMessageEvent(WebSocket conn, ByteBuffer message) {
        super(conn);
        this.message = message;
    }

    public ByteBuffer getMessage() {
        return message;
    }
}
