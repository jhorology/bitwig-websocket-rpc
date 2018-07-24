package com.github.jhorology.bitwig.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberExceptionContext;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;

public class WebSocketRpcServer extends WebSocketServer implements SubscriberExceptionHandler {
    private Logger log;
    private AbstractExtension extension;
    private AsyncEventBus eventBus;
    private ProtocolHandler protocol;
    private RpcRegistry registry;

    private WebSocketRpcServer() {
    }
    
    public WebSocketRpcServer(int port, ProtocolHandler protocol, RpcRegistry registry) throws UnknownHostException {
        this(new InetSocketAddress(port), protocol, registry);
    }

    public WebSocketRpcServer(InetSocketAddress address, ProtocolHandler protocol, RpcRegistry registry) {
        super(address);
        this.protocol = protocol;
        this.registry = registry;
    }

    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(WebSocketRpcServer.class);
        eventBus = new AsyncEventBus(e.getAsyncExecutor(), this);
        eventBus.register(protocol);
        start();
        log.info("WebSocket RPC server started.");
    }

    @Subscribe
    public void onExit(ExitEvent e) {
        try {
            stop();
            log.info("WebSocket RPC server stopped.");
        } catch (IOException ex) {
            log.error(ex);
        } catch (InterruptedException ex) {
            log.error(ex);
        } finally {
            eventBus.unregister(protocol);
            extension = null;
        }
    }

    @Override
    public void run() {
        super.run();
        eventBus.post(new StopEvent());
    }

    @Override
    public void onStart() {
        eventBus.post(new StartEvent(this, registry));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        eventBus.post(new OpenEvent(conn, handshake));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        eventBus.post(new CloseEvent(conn, code, reason, remote));
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        eventBus.post(new TextMessageEvent(conn, message));
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        eventBus.post(new BinaryMessageEvent(conn, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        eventBus.post(new ErrorEvent(conn, ex));
    }

    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        log.error("websocket event handling error. event:" +  context.getEvent().toString(), ex);
    }
}
