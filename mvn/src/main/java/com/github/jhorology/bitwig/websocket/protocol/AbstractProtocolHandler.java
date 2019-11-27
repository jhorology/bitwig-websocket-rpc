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

// jdk
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

// bitwig api
import com.google.common.eventbus.Subscribe;

// dependencies
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.BinaryMessageEvent;
import com.github.jhorology.bitwig.websocket.CloseEvent;
import com.github.jhorology.bitwig.websocket.ErrorEvent;
import com.github.jhorology.bitwig.websocket.OpenEvent;
import com.github.jhorology.bitwig.websocket.StartEvent;
import com.github.jhorology.bitwig.websocket.StopEvent;
import com.github.jhorology.bitwig.websocket.TextMessageEvent;

/**
 * An abstract base class of ProtocolHandler.<br>
 * This class provides the environment that is possible for
 * handling websocket in 'Control Surface Session' thread.
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractProtocolHandler.class);
    
    /**
     * an instance of WebSocketServer
     */
    protected WebSocketServer server;
    
    /**
     * an instance of ReflectionRegistry
     */
    protected RpcRegistry registry;

    // this instance is implement PushModel interface or not.
    private boolean pushModel;
    
    @Subscribe
    public final void onStart(StartEvent e) {
        server = e.getWebSocketServer();
        registry = e.getRpcRegistry();
        pushModel = (this instanceof PushModel);
        onStart();
    }

    @Subscribe
    public final void onStop(StopEvent e) {
    }

    @Subscribe
    public void onOpen(OpenEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("new connection. conn:{}\n\tremoteAddress:{}\n\tresourceDescriptor:{}",
                      e.getConnection(),
                      remoteAddress(e.getConnection()),
                      e.getHandshake().getResourceDescriptor());
        }
        onOpen(e.getConnection(), e.getHandshake());
    }

    @Subscribe
    public void onColse(CloseEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("connection closed. conn:{}\n\tcode:{}\n\treason:{}\n\tremote:{}",
                      e.getConnection(),
                      e.getCode(),
                      e.getReason(),
                      e.isRemote());
        }
        if (this instanceof PushModel) {
            registry.disconnect(e.getConnection());
        }
        onClose(e.getConnection(), e.getCode(), e.getReason(), e.isRemote());
    }
    
    @Subscribe
    public void onMessage(TextMessageEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("a message recieved from:{}\n  --> {}",
                      remoteAddress(e.getConnection()),
                      e.getMessage());
        }
        RequestContext.init(e.getConnection(), registry,
                            pushModel ? (PushModel)this : null);
        onMessage(e.getConnection(), e.getMessage());
        afterRequest(e.getConnection());
    }
    
    @Subscribe
    public void onMessage(BinaryMessageEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("a message recieved from:{}\n  --> {}",
                      remoteAddress(e.getConnection()),
                      e.getMessage());
        }
        RequestContext.init(e.getConnection(), registry,
                            pushModel ? (PushModel)this : null);
        onMessage(e.getConnection(), e.getMessage());
        afterRequest(e.getConnection());
    }
    
    @Subscribe
    public void onError(ErrorEvent e) {
        LOG.error("error occurred remoteAddress:" + remoteAddress(e.getConnection()), e.getException());
        onError(e.getConnection(), e.getException());
    }

    /**
     * Sends the messages to contextual client.
     * @param message 
     */
    protected void send(String message) {
        send(message, RequestContext.getContext().getConnection());
    }
    
    /**
     * Sends the messages to specidied client.
     * @param message
     * @param conn 
     */
    protected void send(String message, WebSocket conn) {
        conn.send(message);
        if (LOG.isTraceEnabled()) {
            LOG.trace("message sended to {}\n  <-- {}",
                      conn.getRemoteSocketAddress(), message);
        }
    }
    
    /**
     * Push the messages to specidied cliens.
     * @param message
     * @param clients 
     */
    protected void push(String message, Collection<WebSocket> clients) {
        server.broadcast(message, clients);
        if (LOG.isTraceEnabled()) {
            LOG.trace("broadcast message to {} clients.\n  <-- {}",
                      clients.size(),
                      message);
        }
    }
    
    /**
     * Broadcast the message.
     * @param message 
     */
    protected void broadcast(String message) {
        server.broadcast(message);
        if (LOG.isTraceEnabled()) {
            LOG.trace("broadcast message to all {} clients.\n  <-- {}",
                      server.getConnections().size(), message);
        }
    }
    
    /**
     *  processing after request/response sequence.
     */
    private void afterRequest(WebSocket conn) {
        if (pushModel) {
            List<Notification> notifications = RequestContext
                .getContext().getNotifications();
            if (notifications != null && !notifications.isEmpty()) {
                notifications.forEach((n) -> ((PushModel)this).push(n, conn));
            }
        }
    }

    private InetSocketAddress remoteAddress(WebSocket conn) {
        return conn != null
            ? conn.getRemoteSocketAddress()
            : null;
    }
}
