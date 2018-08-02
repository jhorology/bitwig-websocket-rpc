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
import org.java_websocket.WebSocket;

/**
 * An event class that is used to establish AsyncEventBus had beeen full drained.
 */
public class CloseEvent extends AbstractWebSocketEvent {
    private final int code;
    private final String reason;
    private final boolean remote;
    
    /**
     * An event class that is used to establish AsyncEventBus had beeen full drained.
     */
    CloseEvent(WebSocket conn, int code, String reason, boolean remote) {
        super(conn);
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
    
    public boolean isRemote() {
        return remote;
    }
}
