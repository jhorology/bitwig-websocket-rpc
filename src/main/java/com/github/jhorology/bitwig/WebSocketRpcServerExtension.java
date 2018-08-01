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
import java.net.UnknownHostException;

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
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;

/**
 * Bitwig Studio extension to support RPC over WebSocket.
 */
public class WebSocketRpcServerExtension extends AbstractExtension {
    private static final int DEFAULT_NUM_SENDS = 4;
    private static final int DEFAULT_NUM_SCENES = 8;
    // 
    // Browser#createDeviceBrowser(final int numFilterColumnEntries,
    //                             final int numResultsColumnEntries)
    //
    // the size of the window used to navigate the filter column entries.
    private static final int DEFAULT_NUM_FLILTER_COLUMN_ENTRIES = 64;
    // the size of the window used to navigate the results column entries.
    private static final int DEFAULT_NUM_RESULTS_COLUMN_ENTRIES = 64;
    private static final String WEBSOCKET_PREF_CATEGORY = "Websocket RPC(new settings need restart)";
    private static final int DEFAULT_WEBSOCKET_PORT = 8887;
        
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
    protected Object[] createModules() throws Exception {
        ControllerHost host = getHost();
        ExtensionDefinition def = getExtensionDefinition();
        String id = def.getId().toString();
        Transport transport = host.createTransport();
        Application application = host.createApplication(); 
        CursorTrack cursorTrack =
            host.createCursorTrack(id, def.getName(),
                                   DEFAULT_NUM_SENDS,
                                   DEFAULT_NUM_SCENES,
                                   true);   // shouldFollwSection
        PinnableCursorDevice cursorDevice =
            cursorTrack.createCursorDevice(id, def.getName(),
                                           DEFAULT_NUM_SENDS,
                                           CursorDeviceFollowMode.FOLLOW_SELECTION);
        ReflectionRegistry registry = new ReflectionRegistry();
        registry.register("rpc",           Rpc.class,                  new RpcImpl());
        registry.register("test",          Test.class,                 new TestImpl());
        registry.register("application",   Application.class,          application);
        registry.register("transport",     Transport.class,            transport);
        registry.register("cursorTrack",   CursorTrack.class,          cursorTrack);
        registry.register("cursorDevice",  PinnableCursorDevice.class, cursorDevice);
        WebSocketRpcServer server = createServer(registry);
            
        // return subscriber modules of extension event.
        return new Object[] {
            registry,
            server
        };
    }

    private WebSocketRpcServer createServer(RpcRegistry registry) throws UnknownHostException {
        ControllerHost host = getHost();
        // always retun 0.0 at this time, it's useless to use initialization.
        // SettableRangedValue value = host.getPreferences()
        //     .getNumberSetting("Server Port",
        //                       WEBSOCKET_PREF_CATEGORY,
        //                       80, 9999, 1, "",
        //                       DEFAULT_WEBSOCKET_PORT);
        // int port = (int)value.get();
        // Protocols protocol =
        //     BitwigUtils.getPreferenceAsEnum(host, "Protocol", WEBSOCKET_PREF_CATEGORY,
        //                                     e -> e.getDisplayName(),
        //                                     Protocols.JSONRPC20);
        int port = DEFAULT_WEBSOCKET_PORT;
        Protocols protocol = Protocols.JSONRPC20;
        return new WebSocketRpcServer(port, Protocols.newProtocolHandler(protocol), registry);
    }
}
