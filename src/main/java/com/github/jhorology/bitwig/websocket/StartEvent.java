package com.github.jhorology.bitwig.websocket;

import org.java_websocket.server.WebSocketServer;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;

public class StartEvent extends AbstractWebSocketEvent {
    private final WebSocketServer webSocketServer;
    private final ReflectionRegistry registry;

    StartEvent(WebSocketServer webSocketServer, ReflectionRegistry registry) {
        super(null);
        this.webSocketServer = webSocketServer;
        this.registry = registry;
    }
    
    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }
    public ReflectionRegistry getReflectionRegistry() {
        return registry;
    }
}
