package com.github.jhorology.bitwig.websocket.protocol;

import java.net.InetSocketAddress;

import com.google.common.eventbus.Subscribe;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import com.github.jhorology.bitwig.extension.ExecutionContext;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;
import com.github.jhorology.bitwig.websocket.BinaryMessageEvent;
import com.github.jhorology.bitwig.websocket.CloseEvent;
import com.github.jhorology.bitwig.websocket.ErrorEvent;
import com.github.jhorology.bitwig.websocket.OpenEvent;
import com.github.jhorology.bitwig.websocket.StartEvent;
import com.github.jhorology.bitwig.websocket.StopEvent;
import com.github.jhorology.bitwig.websocket.TextMessageEvent;

public abstract class AbstractProtocolHandler implements ProtocolHandler, PushModel {
    protected WebSocketServer server;
    protected ReflectionRegistry registry;
    private Logger log;
    private ExecutionContext context;
    
    @Subscribe
    public final void onStart(StartEvent e) {
        log = Logger.getLogger(this.getClass());
        server = e.getWebSocketServer();
        registry = e.getReflectionRegistry();
        context = ExecutionContext.getContext();
        onStart();
    }

    @Subscribe
    public final void onStop(StopEvent e) {
    }

    @Subscribe
    public void onOpen(OpenEvent e) {
        if (log.isTraceEnabled()) {
            log.trace("new connection. remoteAddress:" + remoteAddress(e.getConnection()) +
                      "\nresourceDescriptor:" + e.getHandshake().getResourceDescriptor());
        }
        onOpen(e.getConnection(), e.getHandshake());
    }

    @Subscribe
    public void onColse(CloseEvent e) {
        if (log.isTraceEnabled()) {
            WebSocket conn = e.getConnection();
            log.trace("connection closed. remoteAddress:" + remoteAddress(e.getConnection()) +
                      "\ncode:" + e.getCode() +
                      "\nreason:" + e.getReason() +
                      "\nremote:" + e.isRemote());
        }
        onClose(e.getConnection(), e.getCode(), e.getReason(), e.isRemote());
    }
    
    @Subscribe
    public void onMessage(TextMessageEvent e) {
        if (log.isTraceEnabled()) {
            log.trace("a message recieved from:" + remoteAddress(e.getConnection()) +
                      "\n --> " + e.getMessage());
        }
        context.set(WebSocket.class, e.getConnection());
        context.set(ReflectionRegistry.class, registry);
        onMessage(e.getConnection(), e.getMessage());
    }
    
    @Subscribe
    public void onMessage(BinaryMessageEvent e) {
        if (log.isTraceEnabled()) {
            log.trace("a message recieved from:" + remoteAddress(e.getConnection()) +
                      "\n --> " + e.getMessage());
        }
        context.set(WebSocket.class, e.getConnection());
        context.set(ReflectionRegistry.class, registry);
        onMessage(e.getConnection(), e.getMessage());
    }
    
    @Subscribe
    public void onError(ErrorEvent e) {
        log.error("error occurred remoteAddress:"  + remoteAddress(e.getConnection()), e.getException());
        onError(e.getConnection(), e.getException());
    }

    @Subscribe
    public void onPush(NotificationEvent e) {
        if (e.getClients() != null) {
            broadcast(server, e.getClients(), e.getNotification());
        } else {
            broadcast(server, e.getNotification());
        }
    }

    private InetSocketAddress remoteAddress(WebSocket conn) {
        return conn != null
            ? conn.getRemoteSocketAddress()
            : null;
    }
}
