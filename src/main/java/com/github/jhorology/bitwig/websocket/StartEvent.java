package com.github.jhorology.bitwig.websocket;

import org.java_websocket.server.WebSocketServer;
import com.github.jhorology.bitwig.rpc.RpcRegistry;

public class StartEvent extends AbstractWebSocketEvent {
    private final WebSocketServer webSocketServer;
    private final RpcRegistry registry;

    StartEvent(WebSocketServer webSocketServer, RpcRegistry registry) {
        super(null);
        this.webSocketServer = webSocketServer;
        this.registry = registry;
    }
    
    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }
    public RpcRegistry getRpcRegistry() {
        return registry;
    }
}
