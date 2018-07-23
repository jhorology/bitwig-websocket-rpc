package com.github.jhorology.bitwig.rpc;

import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.websocket.protocol.Notification;

public interface RpcEvent {
    void subscribe(WebSocket client);
    void subscribeOnce(WebSocket client);
    void unsubscribe(WebSocket client);
    void post(Notification notification);
}
