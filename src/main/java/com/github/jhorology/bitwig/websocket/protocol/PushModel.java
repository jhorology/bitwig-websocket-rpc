package com.github.jhorology.bitwig.websocket.protocol;

import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

public interface PushModel {
    void broadcast(WebSocketServer server, Collection<WebSocket> clients, Notification notification);
    void broadcast(WebSocketServer server, Notification notification);
}
