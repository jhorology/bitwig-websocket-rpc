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
package com.github.jhorology.bitwig.websocket;

// jdk
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

// provided dependencies
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberExceptionContext;

// dependencies
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

// source
import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;
import java.util.function.Supplier;

/**
 * An implementation of WebSocketServer.
 * Bassically do nothing in this class, it just exists for dispatching events to 'Control Surface Session" thread.
 */
public class WebSocketRpcServer extends WebSocketServer implements SubscriberExceptionHandler {
    private Logger log;
    private AbstractExtension extension;
    private AsyncEventBus eventBus;
    private ProtocolHandler protocol;
    private RpcRegistry registry;
    private boolean running;
    private boolean fullDrained;
    
    /**
     *  private constructor for prevent instantiation
     */
    private WebSocketRpcServer() {
    }
    
    /**
     * Construct a server.
     * @param port
     * @param protocol
     * @param registry
     * @throws java.net.UnknownHostException
     */
    public WebSocketRpcServer(int port, ProtocolHandler protocol, RpcRegistry registry) throws UnknownHostException {
        this(new InetSocketAddress(port), protocol, registry);
    }

    /**
     *  Construct a server.
     *  @param address
     *  @param protocol
     *  @param registry
     */
    public WebSocketRpcServer(InetSocketAddress address, ProtocolHandler protocol, RpcRegistry registry) {
        // the maximum 4 worker threads is enougn for purpose. 
        super(address, DECODERS <= 4 ? DECODERS : 4);
        this.protocol = protocol;
        this.registry = registry;
    }

    /**
     *  Intialize at extension's start of lifecycle.
     *  @param e
     */
    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(WebSocketRpcServer.class);
        // event bus for dispatching events to 'Control Surface Session' thread.
        eventBus = new AsyncEventBus(e.getAsyncExecutor(), this);
        eventBus.register(protocol);
        start();
        log.info("WebSocket RPC server started.");
    }

    /**
     *  De-intialize at extension's end of lifecycle.
     *  @param e
     */
    @Subscribe
    public void onExit(ExitEvent e) {
        try {
            log.info("waiting for WebSocket RPC server stop.");
            eventBus.post(new StopEvent());
            // --> unnecessary, but it's depend on FlushExecutor
            eventBus.register(this);
            fullDrained = false;
            eventBus.post(new FullDrainedEvent());
            waitFor(() -> fullDrained, 500L);
            // <--
            stop(2000);
            // prevent Address in use error on restart extension.
            waitFor(() -> !running, 2000L);
            log.info("WebSocket RPC server stopped.");
        } catch (InterruptedException ex) {
            log.error(ex);
        } finally {
            eventBus.unregister(this);
            eventBus.unregister(protocol);
            extension = null;
        }
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    private void waitFor(Supplier<Boolean> lambda, long maxMills) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        while (!lambda.get() && elapsedTime < maxMills) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ex) {
                log.error(ex);
            } finally {
                elapsedTime = System.currentTimeMillis() - startTime;
            }
        }
    }
    
    @Override
    public void run() {
        running = true;
        try {
            super.run();
            log.info("WebSocketServer has been stopped.");
        } finally {
            running = false;
        }
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

    /**
     *  To knows AsyncEvent bus is full drained.
     *  @param e
     */
    @Subscribe
    @SuppressWarnings("NonPublicExported")
    public void onFullDrained(FullDrainedEvent e) {
        fullDrained = true;
        log.info("AsyncEventBus has been full drained.");
    }
    
    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        log.error("websocket event handling error. event:" +  context.getEvent().toString(), ex);
    }
}
