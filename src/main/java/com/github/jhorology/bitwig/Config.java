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
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.SettableRangedValue;

// provided dependencies
import com.google.gson.annotations.Expose;

// source
import com.github.jhorology.bitwig.extension.AbstractConfiguration;
import com.github.jhorology.bitwig.extension.ExtensionUtils;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;

public class Config extends AbstractConfiguration {
    private static final Logger LOG = Logger.getLogger(Config.class);
    private static final String WEBSOCKET_PREF_CATEGORY = "Websocket (new settings need restart)";
    private static final String API_PREF_CATEGORY = "API (new settings need restart)";
    private static final String RESTART_PREF_CATEGORY = "Restart";
    private static final int DEFAULT_WEBSOCKET_PORT = 8887;
    private static final int[] INT_OPTIONS_1248 = {1,2,4,8};
    // populate from json -->
    @Expose
    private int webSocketPort = DEFAULT_WEBSOCKET_PORT;
    @Expose
    private Protocols rpcProtocol = Protocols.JSONRPC20;
    @Expose
    private boolean useApplication;
    @Expose
    private boolean useTransport;
    @Expose
    private boolean useCursorTrack;
    @Expose
    private int cursorTrackNumSends = 4;
    @Expose
    private int cursorTrackNumScenes = 8;
    @Expose
    private boolean cursorTrackShouldFollowSelection = true;
    @Expose
    private boolean useCursorDevice;
    @Expose
    private int cursorDeviceNumSends = 4;
    @Expose
    private CursorDeviceFollowMode cursorDeviceFollowMode = CursorDeviceFollowMode.FOLLOW_SELECTION;
    // <--

    /**
     * Returns a configuration value of the WebSocket port number.
     * @return
     */
    public int getWebSocketPort() {
        return webSocketPort;
    }

    /**
     * Returns a configuration value of the RPC protocol.
     * @return
     */
    public Protocols getRpcProtocol() {
        return rpcProtocol;
    }

    /**
     * Return a configuration value of the use or not use Application API.
     * @return
     */
    public boolean useApplication() {
        return useApplication;
    }

    /**
     * Return a configuration value of the use or not use Transport API.
     * @return
     */
    public boolean useTransport() {
        return useTransport;
    }

    /**
     * Return a configuration value of the use or not use CursorTrack API.
     * @return
     */
    public boolean useCursorTrack() {
        return useCursorTrack;
    }

    /**
     * Returns a configuration value of a number of sends of CursorTrack.
     * @return
     */
    public int getCursorTrackNumSends() {
        return cursorTrackNumSends;
    }

    /**
     * Returns a configuration value of the number of scenes of CursorTrack.
     * @return
     */
    public int getCursorTrackNumScenes() {
        return cursorTrackNumScenes;
    }

    /**
     * Return a configuration value of the CursorDevice should follow section or not.
     * @return
     */
    public boolean cursorTrackShouldFollowSelection() {
        return cursorTrackShouldFollowSelection;
    }

    /**
     * Return a configuration value of the use or not use CursorTrack API.
     * @return
     */
    public boolean useCursorDevice() {
        return useCursorDevice;
    }

    /**
     * Returns a number of sends of CursorDevice.
     * @return
     */
    public int getCursorDeviceNumSends() {
        return cursorDeviceNumSends;
    }

    /**
     * Return a configuration value of CursorDeviceFollowMode.
     * @return
     */
    public CursorDeviceFollowMode getCursorDeviceFollowMode() {
        return cursorDeviceFollowMode;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onInit(ControllerHost host) {
        Preferences pref = host.getPreferences();
        SettableRangedValue webSocketPortValue = pref.getNumberSetting
            ("Server Port", WEBSOCKET_PREF_CATEGORY, 80, 9999, 1, "", webSocketPort);
        webSocketPortValue.addRawValueObserver(v -> {
                if (webSocketPort != (int)v) {
                    webSocketPort = (int)v;
                    valueChanged();
                }
            });

        Protocols protocol = ExtensionUtils.getPreferenceAsEnum
            (host, "Protocol", WEBSOCKET_PREF_CATEGORY,
             e -> e.getDisplayName(), rpcProtocol,
             e -> {
                if (rpcProtocol != e) {
                    rpcProtocol = e;
                    valueChanged();
                }
            });

        SettableBooleanValue useApplicationValue = pref.getBooleanSetting
            ("Use Application", API_PREF_CATEGORY, useApplication);
        useApplicationValue.addValueObserver(v -> {
                if (useApplication != v) {
                    useApplication = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useTransportValue = pref.getBooleanSetting
            ("Use Transport", API_PREF_CATEGORY, useTransport);
        useTransportValue.addValueObserver(v -> {
                if (useTransport != v) {
                    useTransport = v;
                    valueChanged();
                }
            });
        // --> CursorTrack
        SettableBooleanValue useCursorTrackValue = pref.getBooleanSetting
            ("Use CursorTrack", API_PREF_CATEGORY, useCursorTrack);
        useCursorTrackValue.addValueObserver(v -> {
                if (useCursorTrack != v) {
                    useCursorTrack = v;
                    valueChanged();
                }
            });
        int cursorTrackNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (host, "CursorTrack sends", API_PREF_CATEGORY, cursorTrackNumSends, INT_OPTIONS_1248, v -> {
                if (cursorTrackNumSends != v) {
                    cursorTrackNumSends = v;
                    valueChanged();
                }
            });

        int cursorTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (host, "CursorTrack scenes", API_PREF_CATEGORY, cursorTrackNumScenes, INT_OPTIONS_1248, v -> {
                if (cursorTrackNumScenes != v) {
                    cursorTrackNumScenes = v;
                    valueChanged();
                }
            });

        SettableBooleanValue cursorTrackShouldFollowSelectionValue = pref.getBooleanSetting
            ("CursorTrack should follow selection", API_PREF_CATEGORY, cursorTrackShouldFollowSelection);
        cursorTrackShouldFollowSelectionValue.addValueObserver(v -> {
                if (cursorTrackShouldFollowSelection != v) {
                    cursorTrackShouldFollowSelection = v;
                    valueChanged();
                }
            });

        // --> CursorDevice
        SettableBooleanValue useCursorDeviceValue = pref.getBooleanSetting
            ("Use CursorDevice (needs CursorTrack)", API_PREF_CATEGORY, useCursorDevice);
        useCursorDeviceValue.addValueObserver(v -> {
                if (useCursorDevice != v) {
                    useCursorDevice = v;
                    valueChanged();
                }
            });

        int cursorDeviceNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (host, "CursorDevice sends", API_PREF_CATEGORY, cursorDeviceNumSends, INT_OPTIONS_1248, v -> {
                if (cursorDeviceNumSends != v) {
                    cursorDeviceNumSends = v;
                    valueChanged();
                }
            });

        CursorDeviceFollowMode cursorDeviceFollowModeValue = ExtensionUtils.getPreferenceAsEnum
            (host, "CursorDevice follow mode", API_PREF_CATEGORY, cursorDeviceFollowMode, v -> {
                if (cursorDeviceFollowMode != v) {
                    cursorDeviceFollowMode = v;
                    valueChanged();
                }
            });


        // for future use
        if (!USE_RC_FILE) {
            webSocketPort = (int)webSocketPortValue.getRaw();
            rpcProtocol = protocol;
            useApplication = useApplicationValue.get();
            useTransport = useTransportValue.get();

            useCursorTrack = useCursorTrackValue.get();
            cursorTrackNumSends = cursorTrackNumSendsValue;
            cursorTrackNumScenes = cursorTrackNumScenesValue;
            cursorTrackShouldFollowSelection = cursorTrackShouldFollowSelectionValue.get();

            useCursorDevice = useCursorDeviceValue.get();
            cursorDeviceNumSends = cursorDeviceNumSendsValue;
            cursorDeviceFollowMode = cursorDeviceFollowModeValue;
        }


        host.getPreferences().getSignalSetting
            ("Restart", RESTART_PREF_CATEGORY, "Restart")
            .addSignalObserver(() -> host.restart());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onExit() {
    }
}
