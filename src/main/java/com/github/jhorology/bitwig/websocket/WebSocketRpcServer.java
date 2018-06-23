package com.github.jhorology.bitwig.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberExceptionContext;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;

public class WebSocketRpcServer extends WebSocketServer implements SubscriberExceptionHandler {
    private AbstractExtension extension;
    private AsyncEventBus eventBus;
    
    public WebSocketRpcServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public WebSocketRpcServer(InetSocketAddress address) {
        super(address);
    }

    @Subscribe
    public void onInitExtension(InitEvent e) {
        extension = e.getExtension();
        eventBus = new AsyncEventBus(extension.getExecutor(), this);
        start();
        log("WebSocket RPC server started.");
    }

    @Subscribe
    public void onExitExtension(ExitEvent e) {
        try {
            stop();
            log("WebSocket RPC server stopped.");
        } catch (IOException ex) {
            log(ex);
        } catch (InterruptedException ex) {
            log(ex);
        } finally {
            extension = null;
        }
    }

    @Override
    public void onStart() {
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log( "new connection:" + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log( "connection closed:" + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    }
    
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log(ex);
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        log( "webbsocket event handling error. event:" +  context.getEvent().toString(), ex);
    }

    private void log(String msg) {
        extension.log(msg);
    }

    private void log(Throwable ex) {
        extension.log(ex);
    }
    
    private void log(String msg, Throwable ex) {
        extension.log(ex);
    }
}
