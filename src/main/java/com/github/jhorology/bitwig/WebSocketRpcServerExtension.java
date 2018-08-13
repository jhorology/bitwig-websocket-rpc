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
import com.bitwig.extension.controller.api.ChainSelector;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ClipLauncherSlotOrSceneBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.Groove;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.TrackBank;
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
            registry.register("application",
                              Application.class,
                              application);
        }
        if (config.useTransport()) {
            Transport transport = host.createTransport();
            registry.register("transport",
                              Transport.class,
                              transport);
        }
        if (config.useGroove()) {
            Groove groove = host.createGroove();
            registry.register("groove",
                              Groove.class,
                              groove);
        }
        if (config.useCursorTrack()) {
            CursorTrack cursorTrack =
                host.createCursorTrack(id, def.getName(),
                                       config.getCursorTrackNumSends(),
                                       config.getCursorTrackNumScenes(),
                                       config.cursorTrackShouldFollowSelection());
            registry.register("cursorTrack",
                              CursorTrack.class,
                              cursorTrack)
                .registerBankItemCount(SendBank.class,
                                       config.getCursorTrackNumSends())
                .registerBankItemCount(ClipLauncherSlotOrSceneBank.class,
                                       config.getCursorTrackNumScenes());

            if (config.useCursorDevice()) {
                PinnableCursorDevice cursorDevice =
                    cursorTrack.createCursorDevice(id, def.getName(),
                                                   config.getCursorDeviceNumSends(),
                                                   config.getCursorDeviceFollowMode());
                registry.register("cursorDevice",
                                  PinnableCursorDevice.class,
                                  cursorDevice)
                    .registerBankItemCount(SendBank.class,
                                           config.getCursorDeviceNumSends());

                if (config.useChainSelector()) {
                    ChainSelector chainSelector = cursorDevice.createChainSelector();
                    registry.register("chainSelector",
                                      ChainSelector.class,
                                      chainSelector)
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }

                if (config.useCursorDeviceLayer()) {
                    CursorDeviceLayer cursorDeviceLayer = cursorDevice.createCursorLayer();
                    registry.register("cursorDeviceLayer",
                                      CursorDeviceLayer.class,
                                      cursorDeviceLayer)
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }

                if (config.useCursorRemoteControlsPage()) {
                    CursorRemoteControlsPage cursorRemoteControlsPage
                        = cursorDevice.createCursorRemoteControlsPage
                        (config.getCursorRemoteControlsPageParameterCount());
                    registry.register("cursorRemoteControlsPage",
                                      CursorRemoteControlsPage.class,
                                      cursorRemoteControlsPage)
                        .registerBankItemCount(CursorRemoteControlsPage.class,
                                               config.getCursorRemoteControlsPageParameterCount());
                }

                if (config.useDrumPadBank()) {
                    DrumPadBank drumPadBank
                        = cursorDevice.createDrumPadBank(config.getDrumPadBankNumPads());
                    registry.register("drumPadBank",
                                      DrumPadBank.class,
                                      drumPadBank)
                        .registerBankItemCount(DrumPadBank.class,
                                               config.getDrumPadBankNumPads())
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }
            }
        }
        if (config.useSceneBank()) {
            SceneBank sceneBank
                = host.createSceneBank(config.getSceneBankNumScenes());
            registry.register("sceneBank",
                              SceneBank.class,
                              sceneBank)
                .registerBankItemCount(SceneBank.class,
                                       config.getSceneBankNumScenes());
        }
        if (config.useMainTrackBank()) {
            TrackBank mainTrackBank
                = host.createMainTrackBank(config.getMainTrackBankNumTracks(),
                                           config.getMainTrackBankNumSends(),
                                           config.getMainTrackBankNumScenes());
            registry.register("mainTrackBank",
                              TrackBank.class,
                              mainTrackBank)
                .registerBankItemCount(TrackBank.class,
                                       config.getMainTrackBankNumTracks())
                .registerBankItemCount(SendBank.class,
                                       config.getMainTrackBankNumSends())
                .registerBankItemCount(ClipLauncherSlotOrSceneBank.class,
                                       config.getMainTrackBankNumScenes());
        }
        if (config.useEffectTrackBank()) {
            TrackBank effectTrackBank
                = host.createEffectTrackBank(config.getEffectTrackBankNumTracks(),
                                             config.getEffectTrackBankNumScenes());
            registry.register("effectTrackBank",
                              TrackBank.class,
                              effectTrackBank)
                .registerBankItemCount(TrackBank.class,
                                       config.getEffectTrackBankNumTracks())
                .registerBankItemCount(ClipLauncherSlotBank.class,
                                       config.getEffectTrackBankNumScenes());
        }
        if (config.useMasterTrack()) {
            MasterTrack masterTrack
                = host.createMasterTrack(config.getMasterTrackNumScenes());
            registry.register("masterTrack",
                              MasterTrack.class,
                              masterTrack)
                .registerBankItemCount(ClipLauncherSlotBank.class,
                                       config.getMasterTrackNumScenes());
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
