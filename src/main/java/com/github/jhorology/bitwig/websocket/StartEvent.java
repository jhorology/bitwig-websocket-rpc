package com.github.jhorology.bitwig.websocket;

import org.java_websocket.server.WebSocketServer;
import com.github.jhorology.bitwig.reflect.MethodRegistry;

public class StartEvent extends AbstractWebSocketEvent {
    private final WebSocketServer webSocketServer;
    private final MethodRegistry methodRegistry;

    StartEvent(WebSocketServer webSocketServer, MethodRegistry methodRegistry) {
        super(null);
        this.webSocketServer = webSocketServer;
        this.methodRegistry = methodRegistry;
    }
    
    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }
    public MethodRegistry getMethodRegistry() {
        return methodRegistry;
    }
}
