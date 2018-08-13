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
    private static final int[] INT_OPTIONS_1TO8  = {1, 2, 4, 8};
    private static final int[] INT_OPTIONS_2TO16 = {2, 4, 8,16};
    private static final int[] INT_OPTIONS_4TO32 = {4, 8,16,32};
    private static final int[] INT_OPTIONS_8TO64 = {8,16,32,64};
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
    private boolean useGroove;
    @Expose
    private boolean useCursorTrack;
    @Expose
    private int cursorTrackNumSends = 2;
    @Expose
    private int cursorTrackNumScenes = 8;
    @Expose
    private boolean cursorTrackShouldFollowSelection = true;
    @Expose
    private boolean useCursorDevice;
    @Expose
    private int cursorDeviceNumSends = 2;
    @Expose
    private CursorDeviceFollowMode cursorDeviceFollowMode = CursorDeviceFollowMode.FOLLOW_SELECTION;
    @Expose
    private boolean useChainSelector;
    @Expose
    private boolean useCursorDeviceLayer;
    @Expose
    private boolean useCursorRemoteControlsPage;
    @Expose
    private int cursorRemoteControlsPageParameterCount = 8;
    @Expose
    private boolean useDrumPadBank;
    @Expose
    private int drumPadBankNumPads = 16;
    @Expose
    private boolean useSceneBank;
    @Expose
    private int sceneBankNumScenes = 8;
    @Expose
    private boolean useMainTrackBank;
    @Expose
    private int mainTrackBankNumTracks = 8;
    @Expose
    private int mainTrackBankNumSends = 2;
    @Expose
    private int mainTrackBankNumScenes = 8;
    @Expose
    private boolean useEffectTrackBank;
    @Expose
    private int effectTrackBankNumTracks = 2;
    @Expose
    private int effectTrackBankNumScenes = 8;
    @Expose
    private boolean useMasterTrack;
    @Expose
    private int masterTrackNumScenes = 8;
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
     * Returns a configuration value of the use or not use Application API.
     * @return
     */
    public boolean useApplication() {
        return useApplication;
    }

    /**
     * Returns a configuration value of the use or not use Transport API.
     * @return
     */
    public boolean useTransport() {
        return useTransport;
    }

    /**
     * Returns a configuration value of the use or not use Groove API.
     * @return
     */
    public boolean useGroove() {
        return useGroove;
    }

    /**
     * Returns a configuration value of the use or not use CursorTrack API.
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
     * Returns a configuration value of the CursorDevice should follow section or not.
     * @return
     */
    public boolean cursorTrackShouldFollowSelection() {
        return cursorTrackShouldFollowSelection;
    }

    /**
     * Returns a configuration value of the use or not use CursorTrack API.
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
     * Returns a configuration value of CursorDeviceFollowMode.
     * @return
     */
    public CursorDeviceFollowMode getCursorDeviceFollowMode() {
        return cursorDeviceFollowMode;
    }

    /**
     * Returns a configuration value of the use or not use ChainSelector API.
     * @return
     */
    public boolean useChainSelector() {
        return useChainSelector;
    }

    /**
     * Returns a configuration value of the use or not use CursorDeviceLayer API.
     * @return
     */
    public boolean useCursorDeviceLayer() {
        return useCursorDeviceLayer;
    }

    /**
     * Returns a configuration value of the use or not use CursorRemoteControlsPage API.
     * @return
     */
    public boolean useCursorRemoteControlsPage() {
        return useCursorRemoteControlsPage;
    }

    /**
     * Returns a configuration value of parameter count of CursorRemoteControlsPage.
     * @return
     */
    public int getCursorRemoteControlsPageParameterCount() {
        return cursorRemoteControlsPageParameterCount;
    };

    /**
     * Returns a configuration value of the use or not use MasterTrack API.
     * @return
     */
    public boolean useDrumPadBank() {
        return useDrumPadBank;
    }

    /**
     * Returns a configuration value of the number of pads of DrumPadBank.
     * @return
     */
    public int getDrumPadBankNumPads() {
        return drumPadBankNumPads;
    }

    /**
     * Returns a configuration value of the use or not use SceneBank API.
     * @return
     */
    public boolean useSceneBank() {
        return useSceneBank;
    }

    /**
     * Returns a configuration value of the number of scenes of SceneBank
     * @return
     */
    public int getSceneBankNumScenes() {
        return sceneBankNumScenes;
    }

    /**
     * Returns a configuration value of the use or not use MainTrackBank API.
     * @return
     */
    public boolean useMainTrackBank() {
        return useMainTrackBank;
    }

    /**
     * Returns a configuration value of the number of tracks of MainTrackBank
     * @return
     */
    public int getMainTrackBankNumTracks() {
        return mainTrackBankNumTracks;
    }

    /**
     * Returns a configuration value of the number of sends of MainTrackBank
     * @return
     */
    public int getMainTrackBankNumSends() {
        return mainTrackBankNumSends;
    }

    /**
     * Returns a configuration value of the number of scenes of MainTrackBank
     * @return
     */
    public int getMainTrackBankNumScenes() {
        return mainTrackBankNumScenes;
    }

    /**
     * Returns a configuration value of the use or not use EffectTrackBank API.
     * @return
     */
    public boolean useEffectTrackBank() {
        return useEffectTrackBank;
    }

    /**
     * Returns a configuration value of the number of tracks of EffectTrackBank
     * @return
     */
    public int getEffectTrackBankNumTracks() {
        return effectTrackBankNumTracks;
    }

    /**
     * Returns a configuration value of the number of scenes of EffectTrackBank
     * @return
     */
    public int getEffectTrackBankNumScenes() {
        return effectTrackBankNumScenes;
    }

    /**
     * Returns a configuration value of the use or not use MasterTrack API.
     * @return
     */
    public boolean useMasterTrack() {
        return useMasterTrack;
    }

    /**
     * Returns a configuration value of the number of scenes of MasterTrack.
     * @return
     */
    public int getMasterTrackNumScenes() {
        return masterTrackNumScenes;
    }


    /**
     * {@inheritDoc}
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
            (pref, "Protocol", WEBSOCKET_PREF_CATEGORY,
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

        SettableBooleanValue useGrooveValue = pref.getBooleanSetting
            ("Use Groove", API_PREF_CATEGORY, useGroove);
        useGrooveValue.addValueObserver(v -> {
                if (useGroove != v) {
                    useGroove = v;
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
            (pref, "CursorTrack sends", API_PREF_CATEGORY, cursorTrackNumSends, INT_OPTIONS_1TO8, v -> {
                if (cursorTrackNumSends != v) {
                    cursorTrackNumSends = v;
                    valueChanged();
                }
            });

        int cursorTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "CursorTrack scenes", API_PREF_CATEGORY, cursorTrackNumScenes, INT_OPTIONS_4TO32, v -> {
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
            (pref, "CursorDevice sends", API_PREF_CATEGORY, cursorDeviceNumSends, INT_OPTIONS_1TO8, v -> {
                if (cursorDeviceNumSends != v) {
                    cursorDeviceNumSends = v;
                    valueChanged();
                }
            });

        CursorDeviceFollowMode cursorDeviceFollowModeValue = ExtensionUtils.getPreferenceAsEnum
            (pref, "CursorDevice follow mode", API_PREF_CATEGORY, cursorDeviceFollowMode, v -> {
                if (cursorDeviceFollowMode != v) {
                    cursorDeviceFollowMode = v;
                    valueChanged();
                }
            });

        // --> ChainSelector
        SettableBooleanValue useChainSelectorValue = pref.getBooleanSetting
            ("Use ChainSelector (needs CursorDevice)", API_PREF_CATEGORY, useChainSelector);
        useChainSelectorValue.addValueObserver(v -> {
                if (useChainSelector != v) {
                    useChainSelector = v;
                    valueChanged();
                }
            });

        // --> CursorDeviceLayer
        SettableBooleanValue useCursorDeviceLayerValue = pref.getBooleanSetting
            ("Use CursorDeviceLayer (needs CursorDevice)", API_PREF_CATEGORY, useCursorDeviceLayer);
        useCursorDeviceLayerValue.addValueObserver(v -> {
                if (useCursorDeviceLayer != v) {
                    useCursorDeviceLayer = v;
                    valueChanged();
                }
            });

        // --> CursorRemoteControlPage
        SettableBooleanValue useCursorRemoteControlsPageValue = pref.getBooleanSetting
            ("Use CursorRemoteControlPage (needs CursorDevice)", API_PREF_CATEGORY, useCursorRemoteControlsPage);
        useCursorRemoteControlsPageValue.addValueObserver(v -> {
                if (useCursorRemoteControlsPage != v) {
                    useCursorRemoteControlsPage = v;
                    valueChanged();
                }
            });

        int cursorRemoteControlsPageParameterCountValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "CursorRemoteControlPage controls", API_PREF_CATEGORY, cursorRemoteControlsPageParameterCount, INT_OPTIONS_4TO32, v -> {
                if (cursorRemoteControlsPageParameterCount != v) {
                    cursorRemoteControlsPageParameterCount = v;
                    valueChanged();
                }
            });

        // --> DrumPadBank
        SettableBooleanValue useDrumPadBankValue = pref.getBooleanSetting
            ("Use DrumPadBank (needs CursorDevice)", API_PREF_CATEGORY, useDrumPadBank);
        useDrumPadBankValue.addValueObserver(v -> {
                if (useDrumPadBank != v) {
                    useDrumPadBank = v;
                    valueChanged();
                }
            });

        int drumPadBankNumPadsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "DrumPadBank pads", API_PREF_CATEGORY, drumPadBankNumPads, INT_OPTIONS_8TO64, v -> {
                if (drumPadBankNumPads != v) {
                    drumPadBankNumPads = v;
                    valueChanged();
                }
            });

        // --> SceneBank
        SettableBooleanValue useSceneBankValue = pref.getBooleanSetting
            ("Use SceneBank", API_PREF_CATEGORY, useSceneBank);
        useSceneBankValue.addValueObserver(v -> {
                if (useSceneBank != v) {
                    useSceneBank = v;
                    valueChanged();
                }
            });

        int sceneBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "SceneBank scenes", API_PREF_CATEGORY, sceneBankNumScenes, INT_OPTIONS_4TO32, v -> {
                if (sceneBankNumScenes != v) {
                    sceneBankNumScenes = v;
                    valueChanged();
                }
            });

        // --> MainTrackBank
        SettableBooleanValue useMainTrackBankValue = pref.getBooleanSetting
            ("Use MainTrackBank", API_PREF_CATEGORY, useMainTrackBank);
        useMainTrackBankValue.addValueObserver(v -> {
                if (useMainTrackBank != v) {
                    useMainTrackBank = v;
                    valueChanged();
                }
            });

        int mainTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "MainTrackBank tracks", API_PREF_CATEGORY, mainTrackBankNumTracks, INT_OPTIONS_4TO32, v -> {
                if (mainTrackBankNumTracks != v) {
                    mainTrackBankNumTracks = v;
                    valueChanged();
                }
            });

        int mainTrackBankNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "MainTrackBank sends", API_PREF_CATEGORY, mainTrackBankNumSends, INT_OPTIONS_1TO8, v -> {
                if (mainTrackBankNumSends != v) {
                    mainTrackBankNumSends = v;
                    valueChanged();
                }
            });

        int mainTrackBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "MainTrackBank scenes", API_PREF_CATEGORY, mainTrackBankNumScenes, INT_OPTIONS_4TO32, v -> {
                if (mainTrackBankNumScenes != v) {
                    mainTrackBankNumScenes = v;
                    valueChanged();
                }
            });

        // --> EffectrackBank
        SettableBooleanValue useEffectTrackBankValue = pref.getBooleanSetting
            ("Use EffectTrackBank", API_PREF_CATEGORY, useEffectTrackBank);
        useEffectTrackBankValue.addValueObserver(v -> {
                if (useEffectTrackBank != v) {
                    useEffectTrackBank = v;
                    valueChanged();
                }
            });

        int effectTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "EffectTrackBank tracks", API_PREF_CATEGORY, effectTrackBankNumTracks, INT_OPTIONS_1TO8, v -> {
                if (effectTrackBankNumTracks != v) {
                    effectTrackBankNumTracks = v;
                    valueChanged();
                }
            });

        int effectTrackBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "EffectTrackBank scenes", API_PREF_CATEGORY, effectTrackBankNumScenes, INT_OPTIONS_4TO32, v -> {
                if (effectTrackBankNumScenes != v) {
                    effectTrackBankNumScenes = v;
                    valueChanged();
                }
            });

        // --> MasterTrack
        SettableBooleanValue useMasterTrackValue = pref.getBooleanSetting
            ("Use MasterTrack", API_PREF_CATEGORY, useMasterTrack);
        useMasterTrackValue.addValueObserver(v -> {
                if (useMasterTrack != v) {
                    useMasterTrack = v;
                    valueChanged();
                }
            });

        int masterTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "MasterTrack scenes", API_PREF_CATEGORY, masterTrackNumScenes, INT_OPTIONS_4TO32, v -> {
                if (masterTrackNumScenes != v) {
                    masterTrackNumScenes = v;
                    valueChanged();
                }
            });

        // for future use
        if (!USE_RC_FILE) {
            webSocketPort = (int)webSocketPortValue.getRaw();
            rpcProtocol = protocol;
            useApplication = useApplicationValue.get();
            useTransport = useTransportValue.get();
            useGroove = useGrooveValue.get();

            useCursorTrack = useCursorTrackValue.get();
            cursorTrackNumSends = cursorTrackNumSendsValue;
            cursorTrackNumScenes = cursorTrackNumScenesValue;
            cursorTrackShouldFollowSelection = cursorTrackShouldFollowSelectionValue.get();

            useCursorDevice = useCursorDeviceValue.get();
            cursorDeviceNumSends = cursorDeviceNumSendsValue;
            cursorDeviceFollowMode = cursorDeviceFollowModeValue;

            useChainSelector = useChainSelectorValue.get();

            useCursorDevice = useCursorDeviceValue.get();

            useCursorRemoteControlsPage = useCursorRemoteControlsPageValue.get();
            cursorRemoteControlsPageParameterCount = cursorRemoteControlsPageParameterCountValue;

            useDrumPadBank = useDrumPadBankValue.get();
            drumPadBankNumPads = drumPadBankNumPadsValue;

            useSceneBank = useSceneBankValue.get();
            sceneBankNumScenes = sceneBankNumScenesValue;

            useMainTrackBank = useMainTrackBankValue.get();
            mainTrackBankNumTracks = mainTrackBankNumTracksValue;
            mainTrackBankNumSends = mainTrackBankNumSendsValue;
            mainTrackBankNumScenes = mainTrackBankNumScenesValue;

            useEffectTrackBank = useEffectTrackBankValue.get();
            effectTrackBankNumTracks = effectTrackBankNumTracksValue;
            effectTrackBankNumScenes = effectTrackBankNumScenesValue;

            useMasterTrack = useMasterTrackValue.get();
            masterTrackNumScenes = masterTrackNumScenesValue;
        }

        host.getPreferences().getSignalSetting
            ("Restart", RESTART_PREF_CATEGORY, "Restart")
            .addSignalObserver(() -> host.restart());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onExit() {
    }
}
