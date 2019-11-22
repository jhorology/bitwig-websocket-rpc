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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

// provided dependencies
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberExceptionContext;

// dependencies
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;

/**
 * An implementation of WebSocketServer.
 * Bassically do nothing in this class, it just exists for dispatching events to 'Control Surface Session" thread.
 */
public class WebSocketRpcServer
    extends WebSocketServer
    implements SubscriberExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketRpcServer.class);

    private AsyncEventBus eventBus;
    private ProtocolHandler protocol;
    private RpcRegistry registry;
    private boolean running;
    private boolean fullDrained;

    /**
     * Construct a server.
     * @param port
     * @param protocol
     * @param registry
     * @throws java.net.UnknownHostException
     */
    public WebSocketRpcServer(int port, ProtocolHandler protocol, RpcRegistry registry)
        throws UnknownHostException {
        this(new InetSocketAddress(port), protocol, registry);
    }

    /**
     * Construct a server.
     * @param address
     * @param protocol
     * @param registry
     */
    public WebSocketRpcServer(InetSocketAddress address, ProtocolHandler protocol, RpcRegistry registry) {
        super(address, Math.min(Runtime.getRuntime().availableProcessors(), 4));
        this.protocol = protocol;
        this.registry = registry;

        // ServerSocket#setReuseAddress()
        //
        // When a TCP connection is closed the connection may remain in a timeout state for a
        // period of time after the connection is closed (typically known as the TIME_WAIT state
        // or 2MSL wait state). For applications using a well known socket address or port it may
        // not be possible to bind a socket to the required SocketAddress if there is a connection
        // in the timeout state involving the socket address or port.
        //
        // Enabling SO_REUSEADDR prior to binding the socket using bind(SocketAddress) allows the 
        // socket to be bound even though a previous connection is in a timeout state.
        //
        // When a ServerSocket is created the initial setting of SO_REUSEADDR is not defined. 
        // Applications can use getReuseAddress() to determine the initial setting of SO_REUSEADDR.
        //
        //The behaviour when SO_REUSEADDR is enabled or disabled after a socket is bound
        // (See isBound()) is not defined.
        setReuseAddr(true);
    }

    /**
     * Initialize at extension's start of life-cycle.<b>
     * This method is called from within 'Control Surfaces Session' thread.
     * @param e
     */
    @Subscribe
    public void onInit(InitEvent<?> e) {
        // event bus for dispatching events to 'Control Surface Session' thread.
        eventBus = new AsyncEventBus(e.getAsyncExecutor(), this);
        eventBus.register(protocol);
        start();
        LOG.info("WebSocket RPC server started on port " + getPort() + ".");
    }

    /**
     * De-initialize at extension's end of life-cycle.
     * This method is called from within 'Control Surfaces Session' thread.
     * @param e
     */
    @Subscribe
    @SuppressWarnings("UseSpecificCatch")
    public void onExit(ExitEvent<?> e) {
        try {
            LOG.info("waiting for WebSocket RPC server stop.");
            eventBus.post(new StopEvent());
            // --> unnecessary, but it's depend on FlushExecutor
            eventBus.register(this);
            fullDrained = false;
            eventBus.post(new FullDrainedEvent());
            waitFor(() -> fullDrained, 500L);
            if (fullDrained) {
                LOG.info("AsyncEventBus has been full drained.");
            }
            // <--
            stop();
            // prevent Address in use error on restart extension.
            waitFor(() -> !running, 2000L);
            if (!running) {
                LOG.info("WebSocketServer has been stopped.");
            }
            LOG.info("WebSocket RPC server stopped.");
        } catch (Exception ex) {
            LOG.error("Error on onExit().", ex);
        } finally {
            eventBus.unregister(this);
            eventBus.unregister(protocol);
        }
    }
    /**
     * An implementation of Runnable#run().<br>
     * A override of {@link WebSocketServer#run()}
     */
    @Override
    public void run() {
        running = true;
        try {
            super.run();
        } finally {
            running = false;
        }
    }

    /**
     * Called when the server started up successfully.<br>
     * If any error occured, onError is called instead.<br>
     * An implementation of {@link WebSocketServer#onStart()}
     */
    @Override
    public void onStart() {
        // dispatch to 'Control Surface Session' thread.
        eventBus.post(new StartEvent(this, registry));
    }

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be written on.<br>
     * An implementation of {@link WebSocketServer#onOpen(WebSocket, ClientHandshake)}
     * @param conn The <tt>WebSocket</tt> instance this event is occuring on.
     * @param handshake The handshake of the websocket instance
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // dispatch to 'Control Surface Session' thread.
        eventBus.post(new OpenEvent(conn, handshake));
    }

    /**
     * Called after the websocket connection has been closed.<br>
     * An implementation of {@link WebSocketServer#onClose(WebSocket, int, String, boolean)}
     * @param conn   The <tt>WebSocket</tt> instance this event is occuring on.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote host.
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // dispatch to 'Control Surface Session' thread.
        eventBus.post(new CloseEvent(conn, code, reason, remote));
    }

    /**
     * Callback for string messages received from the remote host<br>
     * An implementation of {@link WebSocketServer#onMessage(WebSocket, String)}
     * @see #onMessage(WebSocket, ByteBuffer)
     * @param conn The <tt>WebSocket</tt> instance this event is occuring on.
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        // dispatch to 'Control Surface Session' thread.
        eventBus.post(new TextMessageEvent(conn, message));
    }

    /**
     * Callback for binary messages received from the remote host.
     * A override of {@link WebSocketServer#onMessage(WebSocket, ByteBuffer)}
     * @see #onMessage(WebSocket, ByteBuffer)
     * @param conn
     *            The <tt>WebSocket</tt> instance this event is occurring on.
     * @param message
     *            The binary message that was received.
     */
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        // dispatch to 'Control Surface Session' thread.
        eventBus.post(new BinaryMessageEvent(conn, message));
    }

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail
     * {@link #onClose(WebSocket, int, String, boolean)} will be called additionally.<br>
     * This method will be called primarily because of IO or protocol errors.<br>
     * If the given exception is an RuntimeException that probably means that you encountered a bug.<br>
     * An implementation of {@link WebSocketServer#onError(WebSocket, Exception)}
     * @param conn Can be null if there error does not belong to one specific websocket.
     * For example if the servers port could not be bound.
     * @param ex The exception causing this error
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        // dispatch to 'Control Surface Session' thread.
        eventBus.post(new ErrorEvent(conn, ex));
    }

    /**
     * To knows AsyncEvent bus is full drained.
     * @param e
     */
    @Subscribe
    @SuppressWarnings("NonPublicExported")
    public void onFullDrained(FullDrainedEvent e) {
        fullDrained = true;
    }

    /**
     * Handles exceptions thrown by subscribers of eventBus.<br>
     * An implementation of {@link SubscriberExceptionHandler#handleException(Throwable, SubscriberExceptionContext)}
     * @param ex
     * @param context
     */
    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        LOG.error("websocket event handling error. event:" +  context.getEvent().toString(), ex);
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void waitFor(Supplier<Boolean> lambda, long maxMills) {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        while (!lambda.get() && elapsedTime < maxMills) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ex) {
                LOG.error("", ex);
            } finally {
                elapsedTime = System.currentTimeMillis() - startTime;
            }
        }
    }
}
