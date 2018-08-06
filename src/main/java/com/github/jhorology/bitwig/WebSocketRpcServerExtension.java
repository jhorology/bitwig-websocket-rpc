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

// jdk

// bitwig api
import com.bitwig.extension.ExtensionDefinition;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.Transport;

// source
import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;
import com.github.jhorology.bitwig.rpc.Rpc;
import com.github.jhorology.bitwig.rpc.RpcImpl;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;

/**
 * Bitwig Studio extension to support RPC over WebSocket.
 */
public class WebSocketRpcServerExtension extends AbstractExtension<Config> {
    /**
     * Constructor
     * @param definition
     * @param host 
     * @param config 
     */
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host, Config config) {
        super(definition, host, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object[] createModules() throws Exception {
        ControllerHost host = getHost();
        ExtensionDefinition def = getExtensionDefinition();
        String id = def.getId().toString();
        Transport transport = host.createTransport();
        Application application = host.createApplication(); 
        CursorTrack cursorTrack =
            host.createCursorTrack(id, def.getName(),
                                   config.getNumSends(),
                                   config.getNumScenes(),
                                   true);   // shouldFollwSection
        PinnableCursorDevice cursorDevice =
            cursorTrack.createCursorDevice(id, def.getName(),
                                           config.getNumSends(),
                                           CursorDeviceFollowMode.FOLLOW_SELECTION);
        ReflectionRegistry registry = new ReflectionRegistry();
        registry.register("rpc",           Rpc.class,                  new RpcImpl());
        registry.register("test",          Test.class,                 new TestImpl());
        registry.register("application",   Application.class,          application);
        registry.register("transport",     Transport.class,            transport);
        registry.register("cursorTrack",   CursorTrack.class,          cursorTrack);
        registry.register("cursorDevice",  PinnableCursorDevice.class, cursorDevice);
        // return subscriber modules of extension event.
        return new Object[] {
            registry,
            new WebSocketRpcServer(config.getWebSocketPort(),
                                   Protocols.newProtocolHandler(config.getRpcProtocol()),
                                   registry)
        };
    }
}
