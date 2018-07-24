/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.websocket.protocol;

import java.net.InetSocketAddress;

import com.google.common.eventbus.Subscribe;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.BinaryMessageEvent;
import com.github.jhorology.bitwig.websocket.CloseEvent;
import com.github.jhorology.bitwig.websocket.ErrorEvent;
import com.github.jhorology.bitwig.websocket.OpenEvent;
import com.github.jhorology.bitwig.websocket.StartEvent;
import com.github.jhorology.bitwig.websocket.StopEvent;
import com.github.jhorology.bitwig.websocket.TextMessageEvent;

/**
 * An abstract base class of ProtocolHandler.
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {
    
    /**
     * an instance of WebSocketServer
     */
    protected WebSocketServer server;
    
    /**
     * an instance of ReflectionRegistry
     */
    protected RpcRegistry registry;

    private Logger log;
    
    @Subscribe
    public final void onStart(StartEvent e) {
        log = Logger.getLogger(this.getClass());
        server = e.getWebSocketServer();
        registry = e.getRpcRegistry();
        if (this instanceof PushModel) {
            registry.subscribePushEvent(this);
        }
        onStart();
    }

    @Subscribe
    public final void onStop(StopEvent e) {
        if (this instanceof PushModel) {
            registry.unsubscribePushEvent(this);
        }
        onStop();
    }

    @Subscribe
    public void onOpen(OpenEvent e) {
        if (Logger.isTraceEnabled()) {
            log.trace("new connection. remoteAddress:" + remoteAddress(e.getConnection()) +
                      "\nresourceDescriptor:" + e.getHandshake().getResourceDescriptor());
        }
        onOpen(e.getConnection(), e.getHandshake());
    }

    @Subscribe
    public void onColse(CloseEvent e) {
        if (Logger.isTraceEnabled()) {
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
        if (Logger.isTraceEnabled()) {
            log.trace("a message recieved from:" + remoteAddress(e.getConnection()) +
                      "\n --> " + e.getMessage());
        }
        RequestContext.init(e.getConnection(), registry,
                            (this instanceof PushModel) ? (PushModel)this : null);
        onMessage(e.getConnection(), e.getMessage());
    }
    
    @Subscribe
    public void onMessage(BinaryMessageEvent e) {
        if (Logger.isTraceEnabled()) {
            log.trace("a message recieved from:" + remoteAddress(e.getConnection()) +
                      "\n --> " + e.getMessage());
        }
        RequestContext.init(e.getConnection(), registry,
                            (this instanceof PushModel) ? (PushModel)this : null);
        onMessage(e.getConnection(), e.getMessage());
    }
    
    @Subscribe
    public void onError(ErrorEvent e) {
        log.error("error occurred remoteAddress:"  + remoteAddress(e.getConnection()), e.getException());
        onError(e.getConnection(), e.getException());
    }

    @Subscribe
    public void onPush(NotificationEvent e) {
        if (this instanceof PushModel) {
            PushModel model = (PushModel)this;
            if (e.getClients() != null) {
                model.push(e.getNotification(), e.getClients());
            } else {
                model.broadcast(e.getNotification());
            }
        }
    }

    private InetSocketAddress remoteAddress(WebSocket conn) {
        return conn != null
            ? conn.getRemoteSocketAddress()
            : null;
    }
}
