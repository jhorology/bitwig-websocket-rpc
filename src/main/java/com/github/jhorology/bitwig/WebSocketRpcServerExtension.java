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
package com.github.jhorology.bitwig;

import java.net.UnknownHostException;

import com.bitwig.extension.controller.api.ControllerHost;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;

/**
 * Bitwig Studio extension to support RPC over WebSocket.
 */
public class WebSocketRpcServerExtension extends AbstractExtension {
    /**
     * Constructor
     * @param definition
     * @param host 
     */
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] createModules() throws UnknownHostException {
        ReflectionRegistry registry = new ReflectionRegistry();
        return new Object[] {
            registry,
            new WebSocketRpcServer(8887, Protocols.newJsonRpc20(), registry)
        };
    }
}
