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

// dependencies
import com.google.gson.annotations.Expose;

// source
import com.github.jhorology.bitwig.extension.AbstractConfiguration;
import com.github.jhorology.bitwig.extension.ExtensionUtils;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;

public class Config extends AbstractConfiguration {
    private static final Logger LOG = Logger.getLogger(Config.class);
    private static final String WEBSOCKET_PREF_CATEGORY = "Websocket(new settings need restart)";
    private static final int DEFAULT_WEBSOCKET_PORT = 8887;
    private static final int DEFAULT_NUM_SENDS = 4;
    private static final int DEFAULT_NUM_SCENES = 8;
    
    // populate from json -->
    @Expose
    private int webSocketPort = DEFAULT_WEBSOCKET_PORT;
    @Expose
    private Protocols rpcProtocol = Protocols.JSONRPC20;
    @Expose
    private int numSends = DEFAULT_NUM_SENDS;
    @Expose
    private int numScenes = DEFAULT_NUM_SCENES;
    // <--

    /**
     * Return a configuration value of the WebSocket port number.
     * @return 
     */
    public int getWebSocketPort() {
        return webSocketPort;
    }
    
    /**
     * Return a configuration value of the RPC protocol.
     * @return 
     */
    public Protocols getRpcProtocol() {
        return rpcProtocol;
    }
    
    /**
     * Return a configuration value of the number of sends.
     * @return 
     */
    public int getNumSends() {
        return numSends;
    }
    
    /**
     * Return a configuration value of the number of scenes.
     * @return 
     */
    public int getNumScenes() {
        return numScenes;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void onInit(ControllerHost host) {
        host.getPreferences().getNumberSetting
            ("Server Port", WEBSOCKET_PREF_CATEGORY, 80, 9999, 1, "", webSocketPort)
            .addRawValueObserver((double v) -> {
                    if (webSocketPort != (int)v) {
                        webSocketPort = (int)v;
                        valueChanged();
                    }
                });
        ExtensionUtils.getPreferenceAsEnum
            (host, "Protocol", WEBSOCKET_PREF_CATEGORY,
             e -> e.getDisplayName(), rpcProtocol,
             e -> {
                if (rpcProtocol != e) {
                    rpcProtocol = e;
                    valueChanged();
                }
            });
        host.getPreferences().getSignalSetting
            ("Restart", WEBSOCKET_PREF_CATEGORY, "Restart")
            .addSignalObserver(() -> host.restart());
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void onExit() {
    }

}
