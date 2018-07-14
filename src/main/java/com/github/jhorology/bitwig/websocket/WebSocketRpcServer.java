package com.github.jhorology.bitwig.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;


import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberExceptionContext;

import com.bitwig.extension.controller.api.Transport;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.websocket.protocol.AbstractProtocolHandler;

public class WebSocketRpcServer extends WebSocketServer implements SubscriberExceptionHandler {
    private static final Logger log = Logger.getLogger(WebSocketRpcServer.class);

    private AbstractExtension extension;
    private AsyncEventBus eventBus;
    private AbstractProtocolHandler protocol;
    private Transport transport;
    
    public WebSocketRpcServer(int port, AbstractProtocolHandler protocol) throws UnknownHostException {
        this(new InetSocketAddress(port), protocol);
    }

    public WebSocketRpcServer(InetSocketAddress address, AbstractProtocolHandler protocol) {
        super(address);
        this.protocol = protocol;
    }

    @Subscribe
    public void onInitExtension(InitEvent e) {
        extension = e.getExtension();
        eventBus = new AsyncEventBus(extension.getExecutor(), this);
        eventBus.register(protocol);
        transport = extension.getHost().createTransport();
        Arrays.stream(Transport.class.getMethods()).forEach((method) -> {
                log.info(method.getName());
            });
        start();
        log.info("WebSocket RPC server started.");
    }

    @Subscribe
    public void onExitExtension(ExitEvent e) {
        try {
            stop();
            eventBus.unregister(protocol);
            log.info("WebSocket RPC server stopped.");
        } catch (IOException ex) {
            log.error(ex);
        } catch (InterruptedException ex) {
            log.error(ex);
        } finally {
            extension = null;
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        eventBus.post(new OpenEvent(conn, handshake));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        eventBus.post(new StringMessageEvent(conn, message));
    }

    public void onFragment(WebSocket conn, Framedata fragment) {
        eventBus.post(new FragmentEvent(conn, fragment));
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        eventBus.post(new BufferMessageEvent(conn, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if( conn != null ) {
            eventBus.post(new ErrorEvent(conn, ex));
        }
    }

    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        log.error("websocket event handling error. event:" +  context.getEvent().toString(), ex);
    }
}
