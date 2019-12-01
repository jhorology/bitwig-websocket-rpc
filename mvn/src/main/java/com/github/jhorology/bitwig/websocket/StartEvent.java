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

// dependencies
import org.java_websocket.server.WebSocketServer;


/**
 * An event class for notification of websocket server's start-of-lifecycle.
 */
public class StartEvent extends AbstractWebSocketEvent {
    private final WebSocketServer webSocketServer;

    /**
     * Constructor.
     * @param webSocketServer the instance of {@link WebSocketServer}
W     */
    StartEvent(WebSocketServer webSocketServer) {
        super(null);
        this.webSocketServer = webSocketServer;
    }
    
    /**
     * Get an instance of {@link WebSocketServer}.
     * @return
     */
    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }
}
