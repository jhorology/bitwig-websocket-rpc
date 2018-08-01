package com.github.jhorology.bitwig.websocket.protocol;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public interface ProtocolHandler {
    void onStart();
    void onStop();
    void onOpen(WebSocket conn, ClientHandshake handshake);
    void onClose(WebSocket conn, int code, String reason, boolean remote);
    void onMessage(WebSocket conn, String message);
    void onMessage(WebSocket conn, ByteBuffer message);
    void onError(WebSocket conn, Exception ex);
}
