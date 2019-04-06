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

// dependencies
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.extension.AbstractConfiguration;
import com.github.jhorology.bitwig.extension.ExtensionUtils;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;

public class Config extends AbstractConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private static final String WEBSOCKET_PREF_CATEGORY = "Websocket RPC";
    private static final int DEFAULT_WEBSOCKET_PORT = 8887;
    private static final int[] INT_OPTIONS_1TO8  = {1, 2, 4, 8};
    private static final int[] INT_OPTIONS_2TO16 = {2, 4, 8,16};
    private static final int[] INT_OPTIONS_4TO32 = {4, 8,16,32};
    private static final int[] INT_OPTIONS_8TO64 = {8,16,32,64};
    private static final int[] INT_OPTIONS_16TO128 = {16,32,64,128};
    // populate from json -->
    @Expose
    private int webSocketPort;
    @Expose(serialize = false)
    private Protocols rpcProtocol;
    @Expose
    private boolean useAbbreviatedMethodNames;
    @Expose
    private boolean useProject;
    @Expose
    private boolean useApplication;
    @Expose
    private boolean useTransport;
    @Expose
    private boolean useArranger;
    @Expose
    private int arrangerCueMarkerSize;
    @Expose
    private boolean useGroove;
    @Expose
    private boolean useMixer;
    @Expose
    private boolean useArrangerCursorClip;
    @Expose
    private int arrangerCursorClipGridWidth;
    @Expose
    private int arrangerCursorClipGridHeight;
    @Expose
    private boolean useLauncherCursorClip;
    @Expose
    private int launcherCursorClipGridWidth;
    @Expose
    private int launcherCursorClipGridHeight;
    @Expose
    private boolean useCursorTrack;
    @Expose
    private int cursorTrackNumSends;
    @Expose
    private int cursorTrackNumScenes;
    @Expose
    private boolean cursorTrackShouldFollowSelection;
    @Expose
    private boolean useSiblingsTrackBank;
    @Expose
    private int siblingsTrackBankNumTracks;
    @Expose
    private boolean siblingsTrackBankIncludeEffectTracks;
    @Expose
    private boolean siblingsTrackBankIncludeMasterTrack;
    @Expose
    private boolean useChildTrackBank;
    @Expose
    private int childTrackBankNumTracks;
    @Expose
    private boolean childTrackBankHasFlatList;
    @Expose
    private boolean useCursorDevice;
    @Expose
    private int cursorDeviceNumSends;
    @Expose
    private CursorDeviceFollowMode cursorDeviceFollowMode;
    @Expose
    private boolean useChainSelector;
    @Expose
    private boolean useCursorDeviceLayer;
    @Expose
    private boolean useCursorRemoteControlsPage;
    @Expose
    private int cursorRemoteControlsPageParameterCount;
    @Expose
    private boolean useDeviceLayerBank;
    @Expose
    private int deviceLayerBankNumChannels;
    @Expose
    private boolean useDrumPadBank;
    @Expose
    private int drumPadBankNumPads;
    @Expose
    private boolean useSiblingsDeviceBank;
    @Expose
    private int siblingsDeviceBankNumDevices;
    @Expose
    private boolean useChainDeviceBank;
    @Expose
    private int chainDeviceBankNumDevices;
    @Expose
    private boolean useSceneBank;
    @Expose
    private int sceneBankNumScenes;
    @Expose
    private boolean useMainTrackBank;
    @Expose
    private boolean mainTrackBankFollowCursorTrack;
    @Expose
    private int mainTrackBankNumTracks;
    @Expose
    private int mainTrackBankNumSends;
    @Expose
    private int mainTrackBankNumScenes;
    @Expose
    private boolean useEffectTrackBank;
    @Expose
    private int effectTrackBankNumTracks;
    @Expose
    private int effectTrackBankNumScenes;
    @Expose
    private boolean useMasterTrack;
    @Expose
    private int masterTrackNumScenes;
    @Expose
    private boolean useBrowser;
    @Expose
    private int browserSmartCollectionRows;
    @Expose
    private int browserLocationRows;
    @Expose
    private int browserDeviceRows;
    @Expose
    private int browserCategoryRows;
    @Expose
    private int browserTagRows;
    @Expose
    private int browserDeviceTypeRows;
    @Expose
    private int browserFileTypeRows;
    @Expose
    private int browserCreatorRows;
    @Expose
    private int browserResultsRows;
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
     * Returns a configuration value of the use or not use abbreviated method names.
     * @return
     */
    public boolean useAbbreviatedMethodNames() {
        return useAbbreviatedMethodNames;
    }

    /**
     * Returns a configuration value of the use or not use Project API.
     * @return
     */
    public boolean useProject() {
        return useProject;
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
     * Returns a configuration value of the use or not use Arranger API.
     * @return
     */
    public boolean useArranger() {
        return useArranger;
    }

    /**
     * Returns a configuration value of a number of cue maker of Arranger.
     * @return
     */
    public int getArrangerCueMakerSize() {
        return arrangerCueMarkerSize;
    }

    /**
     * Returns a configuration value of the use or not use Groove API.
     * @return
     */
    public boolean useGroove() {
        return useGroove;
    }

    /**
     * Returns a configuration value of the use or not use Mixer API.
     * @return
     */
    public boolean useMixer() {
        return useMixer;
    }

    /**
     * Returns a configuration value of the use or not use ArrangerCursorClip API.
     * @return
     */
    public boolean useArrangerCursorClip() {
        return useArrangerCursorClip;
    }

    /**
     * Returns a configuration value of a grid width of ArrangerCursorClip
     * @return
     */
    public int getArrangerCursorClipGridWidth() {
        return arrangerCursorClipGridWidth;
    }

    /**
     * Returns a configuration value of a grid height of ArrangerCursorClip
     * @return
     */
    public int getArrangerCursorClipGridHeight() {
        return arrangerCursorClipGridHeight;
    }

    /**
     * Returns a configuration value of the use or not use LauncherCursorClip API.
     * @return
     */
    public boolean useLauncherCursorClip() {
        return useLauncherCursorClip;
    }

    /**
     * Returns a configuration value of a grid width of LauncherCursorClip
     * @return
     */
    public int getLauncherCursorClipGridWidth() {
        return launcherCursorClipGridWidth;
    }

    /**
     * Returns a configuration value of a grid height of LauncherCursorClip
     * @return
     */
    public int getLauncherCursorClipGridHeight() {
        return launcherCursorClipGridHeight;
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
     * Returns a configuration value of the use or not use SiblingsTrackBank of CursorTrack API.
     * @return
     */
    public boolean useSiblingsTrackBank() {
        return useSiblingsTrackBank;
    }

    /**
     * Returns a configuration value of number of tracks of SiblingsTrackBank
     * @return
     */
    public int getSiblingsTrackBankNumTracks() {
        return siblingsTrackBankNumTracks;
    }

    /**
     * Returns a configuration value of SiblingsTrackBank include EffectTracks or not.
     * @return
     */
    public boolean isSiblingsTrackBankIncludeEffectTracks() {
        return siblingsTrackBankIncludeEffectTracks;
    }

    /**
     * Returns a configuration value of SiblingsTrackBank include EffectTracks or not.
     * @return
     */
    public boolean isSiblingsTrackBankIncludeMasterTrack() {
        return siblingsTrackBankIncludeMasterTrack;
    }

    /**
     * Returns a configuration value of the use or not use SiblingsTrackBank of CursorTrack API.
     * @return
     */
    public boolean useChildTrackBank() {
        return useChildTrackBank;
    }

    /**
     * Returns a configuration value of number of tracks of ChildTrackBank
     * @return
     */
    public int getChildTrackBankNumTracks() {
        return childTrackBankNumTracks;
    }

    /**
     * Returns a configuration value of ChildTrackBank has flat list or not.
     * @return
     */
    public boolean isChildTrackBankHasFlatList() {
        return childTrackBankHasFlatList;
    }

    /**
     * Returns a configuration value of the use or not use CursorDevice API.
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
     * Returns a configuration value of the use or not use DeviceLayerBank API.
     * @return
     */
    public boolean useDeviceLayerBank() {
        return useDeviceLayerBank;
    }

    /**
     * Returns a configuration value of the number of channles of DeviceLayerBank.
     * @return
     */
    public int getDeviceLayerBankNumChannels() {
        return deviceLayerBankNumChannels;
    }

    /**
     * Returns a configuration value of the use or not use DrumPadBank API.
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
     * Returns a configuration value of the use or not use MasterTrack API.
     * @return
     */
    public boolean useSiblingsDeviceBank() {
        return useSiblingsDeviceBank;
    }

    /**
     * Returns a configuration value of the number of pads of DrumPadBank.
     * @return
     */
    public int getSiblingsDeviceBankNumDevices() {
        return siblingsDeviceBankNumDevices;
    }

    /**
     * Returns a configuration value of the use or not use MasterTrack API.
     * @return
     */
    public boolean useChainDeviceBank() {
        return useChainDeviceBank;
    }

    /**
     * Returns a configuration value of the number of pads of DrumPadBank.
     * @return
     */
    public int getChainDeviceBankNumDevices() {
        return chainDeviceBankNumDevices;
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
     * Returns a configuration value of the use or not use MainTrackBank API.
     * @return
     */
    public boolean isMainTrackBankFollowCursorTrack() {
        return mainTrackBankFollowCursorTrack;
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
     * Returns a configuration value of the use or not use PopupBrowser API.
     * @return
     */
    public boolean useBrowser() {
        return useBrowser;
    }

    /**
     * Returns a configuration value of the number of rows of smart collection column
     * @return
     */
    public int getBrowserSmartCollectionRows() {
        return browserSmartCollectionRows;
    }

    /**
     * Returns a configuration value of the number of rows of location column
     * @return
     */
    public int getBrowserLocationRows() {
        return browserLocationRows;
    }

    /**
     * Returns a configuration value of the number of rows of device column
     * @return
     */
    public int getBrowserDeviceRows() {
        return browserDeviceRows;
    }

    /**
     * Returns a configuration value of the number of rows of category column
     * @return
     */
    public int getBrowserCategoryRows() {
        return browserCategoryRows;
    }

    /**
     * Returns a configuration value of the number of rows of tag column
     * @return
     */
    public int getBrowserTagRows() {
        return browserTagRows;
    }

    /**
     * Returns a configuration value of the number of rows of device type column
     * @return
     */
    public int getBrowserDeviceTypeRows() {
        return browserDeviceTypeRows;
    }

    /**
     * Returns a configuration value of the number of rows of file type column
     * @return
     */
    public int getBrowserFileTypeRows() {
        return browserDeviceTypeRows;
    }

    /**
     * Returns a configuration value of the number of rows of file type column
     * @return
     */
    public int getBrowserCreatorRows() {
        return browserCreatorRows;
    }

    /**
     * Returns a configuration value of the number of rows of results column
     * @return
     */
    public int getBrowserResultsRows() {
        return browserResultsRows;
    }

    /**
     * Reset to defaults.
     */
    @Override
    protected void resetToDefaults() {
        super.resetToDefaults();
        webSocketPort = DEFAULT_WEBSOCKET_PORT;
        rpcProtocol = Protocols.JSONRPC20;
        useAbbreviatedMethodNames = false;
        useProject = false;
        useApplication = false;
        useTransport = false;
        useArranger = false;
        arrangerCueMarkerSize = 16;
        useGroove = false;
        useMixer = false;
        useArrangerCursorClip = false;
        arrangerCursorClipGridWidth = 16;
        arrangerCursorClipGridHeight = 16;
        useLauncherCursorClip = false;
        launcherCursorClipGridWidth = 16;
        launcherCursorClipGridHeight = 16;

        useCursorTrack = false;
        cursorTrackNumSends = 2;
        cursorTrackNumScenes = 8;
        cursorTrackShouldFollowSelection = true;

        useSiblingsTrackBank = false;
        siblingsTrackBankNumTracks = 8;
        siblingsTrackBankIncludeEffectTracks = false;
        siblingsTrackBankIncludeMasterTrack = false;

        useChildTrackBank = false;
        childTrackBankNumTracks = 8;
        childTrackBankHasFlatList = false;

        useCursorDevice = false;
        cursorDeviceNumSends = 2;
        cursorDeviceFollowMode = CursorDeviceFollowMode.FOLLOW_SELECTION;

        useChainSelector = false;

        useCursorDeviceLayer = false;

        useCursorRemoteControlsPage = false;
        cursorRemoteControlsPageParameterCount = 8;

        useDeviceLayerBank = false;
        deviceLayerBankNumChannels = 8;

        useDrumPadBank = false;
        drumPadBankNumPads = 16;

        useSiblingsDeviceBank = false;
        siblingsDeviceBankNumDevices = 4;

        useChainDeviceBank = false;
        chainDeviceBankNumDevices = 4;

        useSceneBank = false;
        sceneBankNumScenes = 8;

        useMainTrackBank = false;
        mainTrackBankFollowCursorTrack = true;
        mainTrackBankNumTracks = 8;
        mainTrackBankNumSends = 2;
        mainTrackBankNumScenes = 8;

        useEffectTrackBank = false;
        effectTrackBankNumTracks = 2;
        effectTrackBankNumScenes = 8;

        useMasterTrack = false;
        masterTrackNumScenes = 8;

        useBrowser = false;
        browserSmartCollectionRows = 32;
        browserLocationRows = 32;
        browserDeviceRows =32;
        browserCategoryRows = 32;
        browserTagRows = 32;
        browserDeviceTypeRows = 16;
        browserFileTypeRows = 16;
        browserCreatorRows = 32;
        browserResultsRows = 32;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInit(ControllerHost host) {
        Preferences pref = host.getPreferences();
        SettableRangedValue webSocketPortValue = pref.getNumberSetting
            ("Server Port", WEBSOCKET_PREF_CATEGORY, 80, 9999, 1, "", DEFAULT_WEBSOCKET_PORT);
        webSocketPortValue.addRawValueObserver(v -> {
                if (ignoreValueChanged) {
                    webSocketPortValue.setRaw(webSocketPort);
                } else if (webSocketPort != (int)v) {
                    webSocketPort = (int)v;
                    valueChanged();
                }
            });

        Protocols protocol = ExtensionUtils.getPreferenceAsEnum
            (pref, "Protocol", WEBSOCKET_PREF_CATEGORY,
             e -> e.getDisplayName(), Protocols.JSONRPC20, rpcProtocol,
             (e, v) -> {
                if (ignoreValueChanged) {
                    v.set(Protocols.JSONRPC20.getDisplayName());
                } else if (rpcProtocol != e) {
                    rpcProtocol = e;
                    valueChanged();
                }
            });

        // who can understand this preferences...
        if (isProduction()) {
            return;
        }
        
        SettableBooleanValue useAbbreviatedMethodNamesValue = pref.getBooleanSetting
            ("Use abbreviated method names", WEBSOCKET_PREF_CATEGORY, true);
        useAbbreviatedMethodNamesValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useAbbreviatedMethodNamesValue.set(useAbbreviatedMethodNames);
                } else if (useAbbreviatedMethodNames != v) {
                    useAbbreviatedMethodNames = v;
                    valueChanged();
                }
            });

        // SettableBooleanValue useProjectValue = pref.getBooleanSetting
        //     ("Use", "Project", false);
        // useProjectValue.set(useProject);
        // useProjectValue.addValueObserver(v -> {
        //         if (useProject != v) {
        //             useProject = v;
        //             valueChanged();
        //         }
        //     });

        SettableBooleanValue useApplicationValue = pref.getBooleanSetting
            ("Use", "Application", false);
        useApplicationValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useApplicationValue.set(useApplication);
                } else if (useApplication != v) {
                    useApplication = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useTransportValue = pref.getBooleanSetting
            ("Use", "Transport", false);
        useTransportValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useTransportValue.set(useTransport);
                } else if (useTransport != v) {
                    useTransport = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useArrangerValue = pref.getBooleanSetting
            ("Use", "Arranger", false);
        useArrangerValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useArrangerValue.set(useArranger);
                } else if (useArranger != v) {
                    useArranger = v;
                    valueChanged();
                }
            });

        int arrangerCueMarkerSizeValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "cue markers", "Arranger", INT_OPTIONS_8TO64[1], arrangerCueMarkerSize,
             INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(arrangerCueMarkerSize));
                } else if (arrangerCueMarkerSize != i) {
                    arrangerCueMarkerSize = i;
                    valueChanged();
                }
            });

        SettableBooleanValue useGrooveValue = pref.getBooleanSetting
            ("Use", "Groove", false);
        useGrooveValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useGrooveValue.set(useGroove);
                } else if (useGroove != v) {
                    useGroove = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useMixerValue = pref.getBooleanSetting
            ("Use", "Mixer", false);
        useMixerValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useMixerValue.set(useMixer);
                } else if (useMixer != v) {
                    useMixer = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useArrangerCursorClipValue = pref.getBooleanSetting
            ("Use", "ArrangerCursorClip", false);
        useArrangerCursorClipValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useArrangerCursorClipValue.set(useArrangerCursorClip);
                } else if (useArrangerCursorClip != v) {
                    useArrangerCursorClip = v;
                    valueChanged();
                }
            });

        int arrangerCursorClipGridWidthValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Grid Width", "ArrangerCursorClip", INT_OPTIONS_8TO64[1], arrangerCursorClipGridWidth,
             INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(arrangerCursorClipGridWidth));
                } else if (arrangerCursorClipGridWidth != i) {
                    arrangerCursorClipGridWidth = i;
                    valueChanged();
                }
            });

        int arrangerCursorClipGridHeightValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Grid Height", "ArrangerCursorClip", INT_OPTIONS_8TO64[1], arrangerCursorClipGridHeight,
             INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(arrangerCursorClipGridHeight));
                } else if (arrangerCursorClipGridHeight != i) {
                    arrangerCursorClipGridHeight = i;
                    valueChanged();
                }
            });

        SettableBooleanValue useLauncherCursorClipValue = pref.getBooleanSetting
            ("Use", "LauncherCursorClip", false);
        useLauncherCursorClipValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useLauncherCursorClipValue.set(useLauncherCursorClip);
                } else if (useLauncherCursorClip != v) {
                    useLauncherCursorClip = v;
                    valueChanged();
                }
            });

        int launcherCursorClipGridWidthValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Grid Width", "LauncherCursorClip", INT_OPTIONS_8TO64[1], launcherCursorClipGridWidth,
             INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(launcherCursorClipGridWidth));
                } else if (launcherCursorClipGridWidth != i) {
                    launcherCursorClipGridWidth = i;
                    valueChanged();
                }
            });

        int launcherCursorClipGridHeightValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Grid Height", "LauncherCursorClip", INT_OPTIONS_8TO64[1], launcherCursorClipGridHeight,
             INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(launcherCursorClipGridHeight));
                } else if (launcherCursorClipGridHeight != i) {
                    launcherCursorClipGridHeight = i;
                    valueChanged();
                }
            });


        // --> CursorTrack
        SettableBooleanValue useCursorTrackValue = pref.getBooleanSetting
            ("Use", "CursorTrack", false);
        useCursorTrackValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useCursorTrackValue.set(useCursorTrack);
                } else if (useCursorTrack != v) {
                    useCursorTrack = v;
                    valueChanged();
                }
            });

        int cursorTrackNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Sends", "CursorTrack", INT_OPTIONS_1TO8[1], cursorTrackNumSends,
             INT_OPTIONS_1TO8, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(cursorTrackNumSends));
                } else if (cursorTrackNumSends != i) {
                    cursorTrackNumSends = i;
                    valueChanged();
                }
            });

        int cursorTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "CursorTrack", INT_OPTIONS_1TO8[1], cursorTrackNumScenes,
             INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(cursorTrackNumScenes));
                } else if (cursorTrackNumScenes != i) {
                    cursorTrackNumScenes = i;
                    valueChanged();
                }
            });

        SettableBooleanValue cursorTrackShouldFollowSelectionValue = pref.getBooleanSetting
            ("Should follow selection", "CursorTrack", false);
        cursorTrackShouldFollowSelectionValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    cursorTrackShouldFollowSelectionValue.set(cursorTrackShouldFollowSelection);
                } else if (cursorTrackShouldFollowSelection != v) {
                    cursorTrackShouldFollowSelection = v;
                    valueChanged();
                }
            });

        // --> SiblingsTrackBank
        SettableBooleanValue useSiblingsTrackBankValue = pref.getBooleanSetting
            ("Use", "SiblingsTrackBank (needs CursorTrack)", false);
        useSiblingsTrackBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useSiblingsTrackBankValue.set(useSiblingsTrackBank);
                } else  if (useSiblingsTrackBank != v) {
                    useSiblingsTrackBank = v;
                    valueChanged();
                }
            });

        int siblingsTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "SiblingsTrackBank (needs CursorTrack)", INT_OPTIONS_4TO32[1],
             siblingsTrackBankNumTracks, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(siblingsTrackBankNumTracks));
                } else if (siblingsTrackBankNumTracks != i) {
                    siblingsTrackBankNumTracks = i;
                    valueChanged();
                }
            });

        SettableBooleanValue siblingsTrackBankIncludeEffectTracksValue = pref.getBooleanSetting
            ("Include effect tracks", "SiblingsTrackBank (needs CursorTrack)", false);
        siblingsTrackBankIncludeEffectTracksValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    siblingsTrackBankIncludeEffectTracksValue.set(siblingsTrackBankIncludeEffectTracks);
                } else if (siblingsTrackBankIncludeEffectTracks != v) {
                    siblingsTrackBankIncludeEffectTracks = v;
                    valueChanged();
                }
            });

        SettableBooleanValue siblingsTrackBankIncludeMasterTrackValue = pref.getBooleanSetting
            ("Include master track", "SiblingsTrackBank (needs CursorTrack)", false);
        siblingsTrackBankIncludeMasterTrackValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    siblingsTrackBankIncludeMasterTrackValue.set(siblingsTrackBankIncludeMasterTrack);
                } else  if (siblingsTrackBankIncludeMasterTrack != v) {
                    siblingsTrackBankIncludeMasterTrack = v;
                    valueChanged();
                }
            });

        // --> SiblingsTrackBank
        SettableBooleanValue useChildTrackBankValue = pref.getBooleanSetting
            ("Use", "ChildTrackBank (needs CursorTrack)", false);
        useChildTrackBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useChildTrackBankValue.set(useChildTrackBank);
                } else if (useChildTrackBank != v) {
                    useChildTrackBank = v;
                    valueChanged();
                }
            });

        int childTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "ChildTrackBank (needs CursorTrack)", INT_OPTIONS_4TO32[1],
             childTrackBankNumTracks, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(childTrackBankNumTracks));
                } else if (childTrackBankNumTracks != i) {
                    childTrackBankNumTracks = i;
                    valueChanged();
                }
            });

        SettableBooleanValue childTrackBankHasFlatListValue = pref.getBooleanSetting
            ("Has flat track list", "ChildTrackBank (needs CursorTrack)", false);
        childTrackBankHasFlatListValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    childTrackBankHasFlatListValue.set(childTrackBankHasFlatList);
                } else if (childTrackBankHasFlatList != v) {
                    childTrackBankHasFlatList = v;
                    valueChanged();
                }
            });

        // --> CursorDevice
        SettableBooleanValue useCursorDeviceValue = pref.getBooleanSetting
            ("Use", "CursorDevice (needs CursorTrack)", false);
        useCursorDeviceValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useCursorDeviceValue.set(useCursorDevice);
                } else if (useCursorDevice != v) {
                    useCursorDevice = v;
                    valueChanged();
                }
            });

        int cursorDeviceNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "sends", "CursorDevice (needs CursorTrack)", INT_OPTIONS_1TO8[1],
             cursorDeviceNumSends, INT_OPTIONS_1TO8, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(cursorDeviceNumSends));
                } else if (cursorDeviceNumSends != i) {
                    cursorDeviceNumSends = i;
                    valueChanged();
                }
            });

        CursorDeviceFollowMode cursorDeviceFollowModeValue = ExtensionUtils.getPreferenceAsEnum
            (pref, "Follow mode", "CursorDevice (needs CursorTrack)", CursorDeviceFollowMode.FOLLOW_SELECTION,
             cursorDeviceFollowMode, (e, v) -> {
                if (ignoreValueChanged) {
                    v.set(cursorDeviceFollowMode.name());
                } else if (cursorDeviceFollowMode != e) {
                    cursorDeviceFollowMode = e;
                    valueChanged();
                }
            });

        // --> ChainSelector
        SettableBooleanValue useChainSelectorValue = pref.getBooleanSetting
            ("Use", "ChainSelector (needs CursorDevice)", false);
        useChainSelectorValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useChainSelectorValue.set(useChainSelector);
                } else if (useChainSelector != v) {
                    useChainSelector = v;
                    valueChanged();
                }
            });

        // --> CursorDeviceLayer
        SettableBooleanValue useCursorDeviceLayerValue = pref.getBooleanSetting
            ("Use", "CursorDeviceLayer (needs CursorDevice)", false);
        useCursorDeviceLayerValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useChainSelectorValue.set(useCursorDeviceLayer);
                } else if (useCursorDeviceLayer != v) {
                    useCursorDeviceLayer = v;
                    valueChanged();
                }
            });

        // --> CursorRemoteControlPage
        SettableBooleanValue useCursorRemoteControlsPageValue = pref.getBooleanSetting
            ("Use", "CursorRemoteControlPage (needs CursorDevice)", false);
        useCursorRemoteControlsPageValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useCursorRemoteControlsPageValue.set(useCursorRemoteControlsPage);
                } else if (useCursorRemoteControlsPage != v) {
                    useCursorRemoteControlsPage = v;
                    valueChanged();
                }
            });

        int cursorRemoteControlsPageParameterCountValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Controls", "CursorRemoteControlPage (needs CursorDevice)", INT_OPTIONS_4TO32[1],
             cursorRemoteControlsPageParameterCount, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(cursorRemoteControlsPageParameterCount));
                } else if (cursorRemoteControlsPageParameterCount != i) {
                    cursorRemoteControlsPageParameterCount = i;
                    valueChanged();
                }
            });

        // --> DeviceLayerBank
        SettableBooleanValue useDeviceLayerBankValue = pref.getBooleanSetting
            ("Use", "DeviceLayerBank (needs CursorDevice)", false);
        useDeviceLayerBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useDeviceLayerBankValue.set(useDeviceLayerBank);
                } else if (useDeviceLayerBank != v) {
                    useDeviceLayerBank = v;
                    valueChanged();
                }
            });

        int deviceLayerBankNumChannelsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Channels", "DeviceLayerBank (needs CursorDevice)", INT_OPTIONS_4TO32[1],
             deviceLayerBankNumChannels, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(deviceLayerBankNumChannels));
                } else if (deviceLayerBankNumChannels != i) {
                    deviceLayerBankNumChannels = i;
                    valueChanged();
                }
            });

        // --> DrumPadBank
        SettableBooleanValue useDrumPadBankValue = pref.getBooleanSetting
            ("Use", "DrumPadBank (needs CursorDevice)", false);
        useDrumPadBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useDrumPadBankValue.set(useDrumPadBank);
                } else if (useDrumPadBank != v) {
                    useDrumPadBank = v;
                    valueChanged();
                }
            });

        int drumPadBankNumPadsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Pads", "DrumPadBank (needs CursorDevice)", INT_OPTIONS_8TO64[1],
             drumPadBankNumPads, INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(drumPadBankNumPads));
                } else if (drumPadBankNumPads != i) {
                    drumPadBankNumPads = i;
                    valueChanged();
                }
            });

        // --> SiblingsDeviceBank
        SettableBooleanValue useSiblingsDeviceBankValue = pref.getBooleanSetting
            ("Use", "SiblingsDeviceBank (needs CursorDevice)", false);
        useSiblingsDeviceBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useSiblingsDeviceBankValue.set(useSiblingsDeviceBank);
                } else if (useSiblingsDeviceBank != v) {
                    useSiblingsDeviceBank = v;
                    valueChanged();
                }
            });

        int siblingsDeviceBankNumDevicesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Devices", "SiblingsDeviceBank (needs CursorDevice)", INT_OPTIONS_2TO16[1],
             siblingsDeviceBankNumDevices, INT_OPTIONS_2TO16, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(siblingsDeviceBankNumDevices));
                } else if (siblingsDeviceBankNumDevices != i) {
                    siblingsDeviceBankNumDevices = i;
                    valueChanged();
                }
            });

        // --> ChainDeviceBank
        SettableBooleanValue useChainDeviceBankValue = pref.getBooleanSetting
            ("Use", "ChainDeviceBank (needs CursorDevice)", false);
        useChainDeviceBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useChainDeviceBankValue.set(useChainDeviceBank);
                } else if (useChainDeviceBank != v) {
                    useChainDeviceBank = v;
                    valueChanged();
                }
            });

        int chainDeviceBankNumDevicesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Devices", "ChainDeviceBank (needs CursorDevice)", INT_OPTIONS_2TO16[1],
             chainDeviceBankNumDevices, INT_OPTIONS_2TO16, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(chainDeviceBankNumDevices));
                } else if (chainDeviceBankNumDevices != i) {
                    chainDeviceBankNumDevices = i;
                    valueChanged();
                }
            });

        // --> SceneBank
        SettableBooleanValue useSceneBankValue = pref.getBooleanSetting
            ("Use", "SceneBank", false);
        useSceneBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useSceneBankValue.set(useSceneBank);
                } else if (useSceneBank != v) {
                    useSceneBank = v;
                    valueChanged();
                }
            });

        int sceneBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "SceneBank", INT_OPTIONS_4TO32[1],
             sceneBankNumScenes, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(sceneBankNumScenes));
                } else if (sceneBankNumScenes != i) {
                    sceneBankNumScenes = i;
                    valueChanged();
                }
            });

        // --> MainTrackBank
        SettableBooleanValue useMainTrackBankValue = pref.getBooleanSetting
            ("Use", "MainTrackBank", false);
        useMainTrackBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useMainTrackBankValue.set(useMainTrackBank);
                } else if (useMainTrackBank != v) {
                    useMainTrackBank = v;
                    valueChanged();
                }
            });

        int mainTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "MainTrackBank", INT_OPTIONS_4TO32[1],
             mainTrackBankNumTracks, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(mainTrackBankNumTracks));
                } else if (mainTrackBankNumTracks != i) {
                    mainTrackBankNumTracks = i;
                    valueChanged();
                }
            });

        int mainTrackBankNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Sends", "MainTrackBank", INT_OPTIONS_1TO8[1],
             mainTrackBankNumSends, INT_OPTIONS_1TO8, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(mainTrackBankNumSends));
                } else if (mainTrackBankNumSends != i) {
                    mainTrackBankNumSends = i;
                    valueChanged();
                }
            });

        int mainTrackBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "MainTrackBank", INT_OPTIONS_4TO32[1],
             mainTrackBankNumScenes, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(mainTrackBankNumScenes));
                } else if (mainTrackBankNumScenes != i) {
                    mainTrackBankNumScenes = i;
                    valueChanged();
                }
            });

        SettableBooleanValue mainTrackBankFollowCursorTrackValue = pref.getBooleanSetting
            ("Follow CursorTrack", "MainTrackBank", false);
        useMainTrackBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    mainTrackBankFollowCursorTrackValue.set(mainTrackBankFollowCursorTrack);
                } else if (mainTrackBankFollowCursorTrack != v) {
                    mainTrackBankFollowCursorTrack = v;
                    valueChanged();
                }
            });


        // --> EffectrackBank
        SettableBooleanValue useEffectTrackBankValue = pref.getBooleanSetting
            ("Use", "EffectTrackBank", false);
        useEffectTrackBankValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useEffectTrackBankValue.set(useEffectTrackBank);
                } else if (useEffectTrackBank != v) {
                    useEffectTrackBank = v;
                    valueChanged();
                }
            });

        int effectTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "EffectTrackBank", INT_OPTIONS_1TO8[1],
             effectTrackBankNumTracks, INT_OPTIONS_1TO8, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(effectTrackBankNumTracks));
                } else if (effectTrackBankNumTracks != i) {
                    effectTrackBankNumTracks = i;
                    valueChanged();
                }
            });

        int effectTrackBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "EffectTrackBank", INT_OPTIONS_4TO32[1],
             effectTrackBankNumScenes, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(effectTrackBankNumScenes));
                } else if (effectTrackBankNumScenes != i) {
                    effectTrackBankNumScenes = i;
                    valueChanged();
                }
            });

        // --> MasterTrack
        SettableBooleanValue useMasterTrackValue = pref.getBooleanSetting
            ("Use", "MasterTrack", false);
        useMasterTrackValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useMasterTrackValue.set(useMasterTrack);
                } else if (useMasterTrack != v) {
                    useMasterTrack = v;
                    valueChanged();
                }
            });

        int masterTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "MasterTrack", INT_OPTIONS_4TO32[1],
             masterTrackNumScenes, INT_OPTIONS_4TO32, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(masterTrackNumScenes));
                } else {
                    if (masterTrackNumScenes != i) {
                        masterTrackNumScenes = i;
                        valueChanged();
                    }
                }
            });

        // --> PopupBrowser
        SettableBooleanValue useBrowserValue = pref.getBooleanSetting
            ("Use", "PopupBrowser", false);
        useBrowserValue.addValueObserver(v -> {
                if (ignoreValueChanged) {
                    useBrowserValue.set(useBrowser);
                } else if (useBrowser != v) {
                    useBrowser = v;
                    valueChanged();
                }
            });

        int browserSmartCollectionRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Smart collection rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserSmartCollectionRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserSmartCollectionRows));
                } else if (browserSmartCollectionRows != i) {
                    browserSmartCollectionRows = i;
                    valueChanged();
                }
            });

        int browserLocationRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Location rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserLocationRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserLocationRows));
                } else if (browserLocationRows != i) {
                    browserLocationRows = i;
                    valueChanged();
                }
            });

        int browserDeviceRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Device rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserDeviceRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserDeviceRows));
                } else if (browserDeviceRows != i) {
                    browserDeviceRows = i;
                    valueChanged();
                }
            });

        int browserCategoryRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Category rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserCategoryRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserCategoryRows));
                } else if (browserCategoryRows != i) {
                    browserCategoryRows = i;
                    valueChanged();
                }
            });

        int browserTagRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tag rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserTagRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserTagRows));
                } else if (browserTagRows != i) {
                    browserTagRows = i;
                    valueChanged();
                }
            });

        int browserDeviceTypeRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Device type rows", "PopupBrowser", INT_OPTIONS_8TO64[1],
             browserDeviceTypeRows, INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserDeviceTypeRows));
                } else if (browserDeviceTypeRows != i) {
                    browserDeviceTypeRows = i;
                    valueChanged();
                }
            });

        int browserFileTypeRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "File type rows", "PopupBrowser", INT_OPTIONS_8TO64[1],
             browserFileTypeRows, INT_OPTIONS_8TO64, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserFileTypeRows));
                } else if (browserFileTypeRows != i) {
                    browserFileTypeRows = i;
                    valueChanged();
                }
            });

        int browserCreatorRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Creator rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserCreatorRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserCreatorRows));
                } else if (browserCreatorRows != i) {
                    browserCreatorRows = i;
                    valueChanged();
                }
            });

        int browserResultsRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Results rows", "PopupBrowser", INT_OPTIONS_16TO128[1],
             browserResultsRows, INT_OPTIONS_16TO128, (i, v) -> {
                if (ignoreValueChanged) {
                    v.set(String.valueOf(browserResultsRows));
                } else if (browserResultsRows != i) {
                    browserResultsRows = i;
                    valueChanged();
                }
            });

        // for future use
        if (!USE_RC_FILE) {
            webSocketPort = (int)webSocketPortValue.getRaw();
            rpcProtocol = protocol;

            // useProject = useProjectValue.get();

            useApplication = useApplicationValue.get();
            useTransport = useTransportValue.get();
            useArranger = useArrangerValue.get();
            arrangerCueMarkerSize = arrangerCueMarkerSizeValue;
            useGroove = useGrooveValue.get();
            useMixer = useMixerValue.get();

            useCursorTrack = useCursorTrackValue.get();
            cursorTrackNumSends = cursorTrackNumSendsValue;
            cursorTrackNumScenes = cursorTrackNumScenesValue;
            cursorTrackShouldFollowSelection = cursorTrackShouldFollowSelectionValue.get();

            useSiblingsTrackBank = useSiblingsTrackBankValue.get();
            siblingsTrackBankNumTracks = siblingsTrackBankNumTracksValue;
            siblingsTrackBankIncludeEffectTracks = siblingsTrackBankIncludeEffectTracksValue.get();
            siblingsTrackBankIncludeMasterTrack = siblingsTrackBankIncludeMasterTrackValue.get();

            useChildTrackBank = useChildTrackBankValue.get();
            childTrackBankNumTracks = childTrackBankNumTracksValue;
            childTrackBankHasFlatList = childTrackBankHasFlatListValue.get();

            useCursorDevice = useCursorDeviceValue.get();
            cursorDeviceNumSends = cursorDeviceNumSendsValue;
            cursorDeviceFollowMode = cursorDeviceFollowModeValue;

            useChainSelector = useChainSelectorValue.get();

            useCursorDevice = useCursorDeviceValue.get();

            useCursorRemoteControlsPage = useCursorRemoteControlsPageValue.get();
            cursorRemoteControlsPageParameterCount = cursorRemoteControlsPageParameterCountValue;

            useDeviceLayerBank = useDeviceLayerBankValue.get();
            deviceLayerBankNumChannels = deviceLayerBankNumChannelsValue;

            useDrumPadBank = useDrumPadBankValue.get();
            drumPadBankNumPads = drumPadBankNumPadsValue;

            useSiblingsDeviceBank = useSiblingsDeviceBankValue.get();
            siblingsDeviceBankNumDevices = siblingsDeviceBankNumDevicesValue;

            useChainDeviceBank = useChainDeviceBankValue.get();
            chainDeviceBankNumDevices = chainDeviceBankNumDevicesValue;

            useSceneBank = useSceneBankValue.get();
            sceneBankNumScenes = sceneBankNumScenesValue;

            useMainTrackBank = useMainTrackBankValue.get();
            mainTrackBankNumTracks = mainTrackBankNumTracksValue;
            mainTrackBankNumSends = mainTrackBankNumSendsValue;
            mainTrackBankNumScenes = mainTrackBankNumScenesValue;
            mainTrackBankFollowCursorTrack = mainTrackBankFollowCursorTrackValue.get();

            useEffectTrackBank = useEffectTrackBankValue.get();
            effectTrackBankNumTracks = effectTrackBankNumTracksValue;
            effectTrackBankNumScenes = effectTrackBankNumScenesValue;

            useMasterTrack = useMasterTrackValue.get();
            masterTrackNumScenes = masterTrackNumScenesValue;

            useBrowser = useBrowserValue.get();
            browserSmartCollectionRows = browserSmartCollectionRowsValue;
            browserLocationRows = browserLocationRowsValue;
            browserDeviceRows = browserDeviceRowsValue;
            browserCategoryRows = browserCategoryRowsValue;
            browserTagRows = browserTagRowsValue;
            browserDeviceTypeRows = browserDeviceTypeRowsValue;
            browserFileTypeRows = browserFileTypeRowsValue;
            browserCreatorRows = browserCreatorRowsValue;
            browserResultsRows = browserResultsRowsValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onExit() {
    }
}
