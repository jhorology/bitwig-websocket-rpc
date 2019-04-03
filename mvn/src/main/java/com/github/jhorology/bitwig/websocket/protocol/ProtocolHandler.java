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
import java.nio.ByteBuffer;

// dependencies
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

/**
 * A factory class for creating RPC protocol handler.
 */
public interface ProtocolHandler {
    /**
     * 
     */
    void onStart();
    
    /**
     * 
     */
    void onStop();
    
    /**
     * 
     * @param conn
     * @param handshake 
     */
    void onOpen(WebSocket conn, ClientHandshake handshake);
    
    /**
     * 
     * @param conn
     * @param code
     * @param reason
     * @param remote 
     */
    void onClose(WebSocket conn, int code, String reason, boolean remote);
    
    /**
     * 
     * @param conn
     * @param message 
     */
    void onMessage(WebSocket conn, String message);
    
    /**
     * 
     * @param conn
     * @param message 
     */
    void onMessage(WebSocket conn, ByteBuffer message);
    
    /**
     * 
     * @param conn
     * @param ex 
     */
    void onError(WebSocket conn, Exception ex);

    /**
     * 
     * @return 
     */
    PushModel getPushModel();
    
    /**
     * Returns specifdid Bitwig type are serializable or not.
     * @param bitwigType
     * @return 
     */
    boolean isSerializableBitwigType(Class<?> bitwigType);
}
