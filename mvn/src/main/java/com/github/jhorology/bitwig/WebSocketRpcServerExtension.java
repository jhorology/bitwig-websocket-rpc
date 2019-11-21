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

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;

// source
import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;

/**
 * Bitwig Studio extension to support RPC over WebSocket.
 */
public class WebSocketRpcServerExtension extends AbstractExtension<Config> {
    /**
     * Constructor
     * @param definition
     * @param host
s     */
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host) {
        super(definition, host, new Config());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] createModules() throws Exception {
        ProtocolHandler protocol =
            Protocols.newProtocolHandler(config.getRpcProtocol());
        ReflectionRegistry registry = new ReflectionRegistry(config, protocol);
        // returns subscriber modules of extension event.
        return new Object[] {
            new WebSocketRpcServer(config.getWebSocketPort(),
                                   protocol,
                                   registry),
            registry
        };
    }
}
