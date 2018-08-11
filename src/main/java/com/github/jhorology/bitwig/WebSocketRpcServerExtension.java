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
import com.bitwig.extension.controller.api.ClipLauncherSlotOrScene;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.Send;
import com.bitwig.extension.controller.api.Transport;

// source
import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;
import com.github.jhorology.bitwig.rpc.Rpc;
import com.github.jhorology.bitwig.rpc.RpcImpl;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;
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
        ProtocolHandler protocol = Protocols.newProtocolHandler(config.getRpcProtocol());
        ReflectionRegistry registry = new ReflectionRegistry(protocol);
        registry.register("rpc",  Rpc.class, new RpcImpl());
        // for test
        registry.register("test", Test.class, new TestImpl());

        if (config.useApplication()) {
            Application application = host.createApplication();
            registry.register("application", Application.class, application);
        }
        if (config.useTransport()) {
            Transport transport = host.createTransport();
            registry.register("transport", Transport.class, transport);
        }
        if (config.useCursorTrack()) {
            CursorTrack cursorTrack =
                host.createCursorTrack(id, def.getName(),
                                       config.getCursorTrackNumSends(),
                                       config.getCursorTrackNumScenes(),
                                       config.cursorTrackShouldFollowSelection());
            registry.register("cursorTrack", CursorTrack.class, cursorTrack)
                .registerBankItemCount(Send.class, config.getCursorTrackNumSends())
                .registerBankItemCount(ClipLauncherSlotOrScene.class, config.getCursorTrackNumScenes());
                    
            if (config.useCursorDevice()) {
                PinnableCursorDevice cursorDevice =
                    cursorTrack.createCursorDevice(id, def.getName(),
                                                   config.getCursorDeviceNumSends(),
                                                   config.getCursorDeviceFollowMode());
                registry.register("cursorDevice", PinnableCursorDevice.class, cursorDevice)
                    .registerBankItemCount(Send.class, config.getCursorDeviceNumSends());
                
                if (config.useCursorRemoteControlsPage()) {
                    CursorRemoteControlsPage cursorRemoteControlsPage
                        = cursorDevice.createCursorRemoteControlsPage
                        (config.getCursorRemoteControlsPageParameterCount());
                    registry.register("cursorRemoteControlsPage", CursorRemoteControlsPage.class, cursorRemoteControlsPage)
                        .registerBankItemCount(RemoteControl.class, config.getCursorRemoteControlsPageParameterCount());
                }
            }
        }
        // returns subscriber modules of extension event.
        return new Object[] {
            registry,
            new WebSocketRpcServer(config.getWebSocketPort(),
                                   protocol,
                                   registry)
        };
    }
}
