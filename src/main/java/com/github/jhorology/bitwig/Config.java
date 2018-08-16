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
    private static final String WEBSOCKET_PREF_CATEGORY = "Websocket RPC";
    private static final int DEFAULT_WEBSOCKET_PORT = 8887;
    private static final int[] INT_OPTIONS_1TO8  = {1, 2, 4, 8};
    private static final int[] INT_OPTIONS_2TO16 = {2, 4, 8,16};
    private static final int[] INT_OPTIONS_4TO32 = {4, 8,16,32};
    private static final int[] INT_OPTIONS_8TO64 = {8,16,32,64};
    private static final int[] INT_OPTIONS_16TO128 = {16,32,64,128};
    // populate from json -->
    @Expose
    private int webSocketPort = DEFAULT_WEBSOCKET_PORT;
    @Expose
    private Protocols rpcProtocol = Protocols.JSONRPC20;
    @Expose
    private boolean useAbbreviatedMethodNames = true;
    @Expose
    private boolean useApplication;
    @Expose
    private boolean useTransport;
    @Expose
    private boolean useArranger;
    @Expose
    private int arrangerCueMarkerSize = 16;
    @Expose
    private boolean useGroove;
    @Expose
    private boolean useMixer;
    @Expose
    private boolean useCursorTrack;
    @Expose
    private int cursorTrackNumSends = 2;
    @Expose
    private int cursorTrackNumScenes = 8;
    @Expose
    private boolean cursorTrackShouldFollowSelection = true;
    @Expose
    private boolean useSiblingsTrackBank;
    @Expose
    private int siblingsTrackBankNumTracks = 8;
    @Expose
    private boolean siblingsTrackBankIncludeEffectTracks;
    @Expose
    private boolean siblingsTrackBankIncludeMasterTrack;
    @Expose
    private boolean useChildTrackBank;
    @Expose
    private int childTrackBankNumTracks = 8;
    @Expose
    private boolean childTrackBankHasFlatList;
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
    private boolean useDeviceLayerBank;
    @Expose
    private int deviceLayerBankNumChannels = 8;
    @Expose
    private boolean useDrumPadBank;
    @Expose
    private int drumPadBankNumPads = 16;
    @Expose
    private boolean useSiblingsDeviceBank;
    @Expose
    private int siblingsDeviceBankNumDevices = 4;
    @Expose
    private boolean useChainDeviceBank;
    @Expose
    private int chainDeviceBankNumDevices = 4;
    @Expose
    private boolean useSceneBank;
    @Expose
    private int sceneBankNumScenes = 8;
    @Expose
    private boolean useMainTrackBank;
    @Expose
    private boolean mainTrackBankFollowCursorTrack;
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
    @Expose
    private boolean useBrowser;
    @Expose
    private int browserSmartCollectionRows = 32;
    @Expose
    private int browserLocationRows = 32;
    @Expose
    private int browserDeviceRows =32;
    @Expose
    private int browserCategoryRows = 32;
    @Expose
    private int browserTagRows = 32;
    @Expose
    private int browserDeviceTypeRows = 16;
    @Expose
    private int browserFileTypeRows = 16;
    @Expose
    private int browserCreatorRows = 32;
    @Expose
    private int browserResultsRows = 32;
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

        SettableBooleanValue useAbbreviatedMethodNamesValue = pref.getBooleanSetting
            ("Use abbreviated method names", WEBSOCKET_PREF_CATEGORY, useAbbreviatedMethodNames);
        useAbbreviatedMethodNamesValue.addValueObserver(v -> {
                if (useAbbreviatedMethodNames != v) {
                    useAbbreviatedMethodNames = v;
                    valueChanged();
                }
            });
        
        SettableBooleanValue useApplicationValue = pref.getBooleanSetting
            ("Use", "Application", useApplication);
        useApplicationValue.addValueObserver(v -> {
                if (useApplication != v) {
                    useApplication = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useTransportValue = pref.getBooleanSetting
            ("Use", "Transport", useTransport);
        useTransportValue.addValueObserver(v -> {
                if (useTransport != v) {
                    useTransport = v;
                    valueChanged();
                }
            });
        
        SettableBooleanValue useArrangerValue = pref.getBooleanSetting
            ("Use", "Arranger", useArranger);
        useArrangerValue.addValueObserver(v -> {
                if (useArranger != v) {
                    useArranger = v;
                    valueChanged();
                }
            });
        
        int arrangerCueMarkerSizeValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "cue markers", "Arranger", arrangerCueMarkerSize, INT_OPTIONS_8TO64, v -> {
                if (arrangerCueMarkerSize != v) {
                    arrangerCueMarkerSize = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useGrooveValue = pref.getBooleanSetting
            ("Use", "Groove", useGroove);
        useGrooveValue.addValueObserver(v -> {
                if (useGroove != v) {
                    useGroove = v;
                    valueChanged();
                }
            });

        SettableBooleanValue useMixerValue = pref.getBooleanSetting
            ("Use", "Mixer", useMixer);
        useMixerValue.addValueObserver(v -> {
                if (useMixer != v) {
                    useMixer = v;
                    valueChanged();
                }
            });
        
        // --> CursorTrack
        SettableBooleanValue useCursorTrackValue = pref.getBooleanSetting
            ("Use", "CursorTrack", useCursorTrack);
        useCursorTrackValue.addValueObserver(v -> {
                if (useCursorTrack != v) {
                    useCursorTrack = v;
                    valueChanged();
                }
            });
        
        int cursorTrackNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Sends", "CursorTrack", cursorTrackNumSends, INT_OPTIONS_1TO8, v -> {
                if (cursorTrackNumSends != v) {
                    cursorTrackNumSends = v;
                    valueChanged();
                }
            });

        int cursorTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "CursorTrack", cursorTrackNumScenes, INT_OPTIONS_4TO32, v -> {
                if (cursorTrackNumScenes != v) {
                    cursorTrackNumScenes = v;
                    valueChanged();
                }
            });

        SettableBooleanValue cursorTrackShouldFollowSelectionValue = pref.getBooleanSetting
            ("Should follow selection", "CursorTrack", cursorTrackShouldFollowSelection);
        cursorTrackShouldFollowSelectionValue.addValueObserver(v -> {
                if (cursorTrackShouldFollowSelection != v) {
                    cursorTrackShouldFollowSelection = v;
                    valueChanged();
                }
            });

        // --> SiblingsTrackBank
        SettableBooleanValue useSiblingsTrackBankValue = pref.getBooleanSetting
            ("Use", "SiblingsTrackBank (needs CursorTrack)", useSiblingsTrackBank);
        useSiblingsTrackBankValue.addValueObserver(v -> {
                if (useSiblingsTrackBank != v) {
                    useSiblingsTrackBank = v;
                    valueChanged();
                }
            });
        
        int siblingsTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "SiblingsTrackBank (needs CursorTrack)", siblingsTrackBankNumTracks, INT_OPTIONS_4TO32, v -> {
                if (siblingsTrackBankNumTracks != v) {
                    siblingsTrackBankNumTracks = v;
                    valueChanged();
                }
            });
        
        SettableBooleanValue siblingsTrackBankIncludeEffectTracksValue = pref.getBooleanSetting
            ("Include effect tracks", "SiblingsTrackBank (needs CursorTrack)", siblingsTrackBankIncludeEffectTracks);
        siblingsTrackBankIncludeEffectTracksValue.addValueObserver(v -> {
                if (siblingsTrackBankIncludeEffectTracks != v) {
                    siblingsTrackBankIncludeEffectTracks = v;
                    valueChanged();
                }
            });
        
        SettableBooleanValue siblingsTrackBankIncludeMasterTrackValue = pref.getBooleanSetting
            ("Include master track", "SiblingsTrackBank (needs CursorTrack)", siblingsTrackBankIncludeMasterTrack);
        siblingsTrackBankIncludeMasterTrackValue.addValueObserver(v -> {
                if (siblingsTrackBankIncludeMasterTrack != v) {
                    siblingsTrackBankIncludeMasterTrack = v;
                    valueChanged();
                }
            });
        
        // --> SiblingsTrackBank
        SettableBooleanValue useChildTrackBankValue = pref.getBooleanSetting
            ("Use", "ChildTrackBank (needs CursorTrack)", useChildTrackBank);
        useChildTrackBankValue.addValueObserver(v -> {
                if (useChildTrackBank != v) {
                    useChildTrackBank = v;
                    valueChanged();
                }
            });
        
        int childTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "ChildTrackBank (needs CursorTrack)", childTrackBankNumTracks, INT_OPTIONS_4TO32, v -> {
                if (childTrackBankNumTracks != v) {
                    childTrackBankNumTracks = v;
                    valueChanged();
                }
            });
        
        SettableBooleanValue childTrackBankHasFlatListValue = pref.getBooleanSetting
            ("Has flat track list", "ChildTrackBank (needs CursorTrack)", childTrackBankHasFlatList);
        childTrackBankHasFlatListValue.addValueObserver(v -> {
                if (childTrackBankHasFlatList != v) {
                    childTrackBankHasFlatList = v;
                    valueChanged();
                }
            });
        
        // --> CursorDevice
        SettableBooleanValue useCursorDeviceValue = pref.getBooleanSetting
            ("Use", "CursorDevice (needs CursorTrack)", useCursorDevice);
        useCursorDeviceValue.addValueObserver(v -> {
                if (useCursorDevice != v) {
                    useCursorDevice = v;
                    valueChanged();
                }
            });

        int cursorDeviceNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "sends", "CursorDevice (needs CursorTrack)", cursorDeviceNumSends, INT_OPTIONS_1TO8, v -> {
                if (cursorDeviceNumSends != v) {
                    cursorDeviceNumSends = v;
                    valueChanged();
                }
            });

        CursorDeviceFollowMode cursorDeviceFollowModeValue = ExtensionUtils.getPreferenceAsEnum
            (pref, "Follow mode", "CursorDevice (needs CursorTrack)", cursorDeviceFollowMode, v -> {
                if (cursorDeviceFollowMode != v) {
                    cursorDeviceFollowMode = v;
                    valueChanged();
                }
            });

        // --> ChainSelector
        SettableBooleanValue useChainSelectorValue = pref.getBooleanSetting
            ("Use", "ChainSelector (needs CursorDevice)", useChainSelector);
        useChainSelectorValue.addValueObserver(v -> {
                if (useChainSelector != v) {
                    useChainSelector = v;
                    valueChanged();
                }
            });

        // --> CursorDeviceLayer
        SettableBooleanValue useCursorDeviceLayerValue = pref.getBooleanSetting
            ("Use", "CursorDeviceLayer (needs CursorDevice)", useCursorDeviceLayer);
        useCursorDeviceLayerValue.addValueObserver(v -> {
                if (useCursorDeviceLayer != v) {
                    useCursorDeviceLayer = v;
                    valueChanged();
                }
            });

        // --> CursorRemoteControlPage
        SettableBooleanValue useCursorRemoteControlsPageValue = pref.getBooleanSetting
            ("Use", "CursorRemoteControlPage (needs CursorDevice)", useCursorRemoteControlsPage);
        useCursorRemoteControlsPageValue.addValueObserver(v -> {
                if (useCursorRemoteControlsPage != v) {
                    useCursorRemoteControlsPage = v;
                    valueChanged();
                }
            });

        int cursorRemoteControlsPageParameterCountValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Controls", "CursorRemoteControlPage (needs CursorDevice)", cursorRemoteControlsPageParameterCount, INT_OPTIONS_4TO32, v -> {
                if (cursorRemoteControlsPageParameterCount != v) {
                    cursorRemoteControlsPageParameterCount = v;
                    valueChanged();
                }
            });

        // --> DeviceLayerBank
        SettableBooleanValue useDeviceLayerBankValue = pref.getBooleanSetting
            ("Use", "DeviceLayerBank (needs CursorDevice)", useDeviceLayerBank);
        useDeviceLayerBankValue.addValueObserver(v -> {
                if (useDeviceLayerBank != v) {
                    useDeviceLayerBank = v;
                    valueChanged();
                }
            });

        int deviceLayerBankNumChannelsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Channels", "DeviceLayerBank (needs CursorDevice)", deviceLayerBankNumChannels, INT_OPTIONS_4TO32, v -> {
                if (deviceLayerBankNumChannels != v) {
                    deviceLayerBankNumChannels = v;
                    valueChanged();
                }
            });
        
        // --> DrumPadBank
        SettableBooleanValue useDrumPadBankValue = pref.getBooleanSetting
            ("Use", "DrumPadBank (needs CursorDevice)", useDrumPadBank);
        useDrumPadBankValue.addValueObserver(v -> {
                if (useDrumPadBank != v) {
                    useDrumPadBank = v;
                    valueChanged();
                }
            });

        int drumPadBankNumPadsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Pads", "DrumPadBank (needs CursorDevice)", drumPadBankNumPads, INT_OPTIONS_8TO64, v -> {
                if (drumPadBankNumPads != v) {
                    drumPadBankNumPads = v;
                    valueChanged();
                }
            });

        // --> SiblingsDeviceBank
        SettableBooleanValue useSiblingsDeviceBankValue = pref.getBooleanSetting
            ("Use", "SiblingsDeviceBank (needs CursorDevice)", useSiblingsDeviceBank);
        useSiblingsDeviceBankValue.addValueObserver(v -> {
                if (useSiblingsDeviceBank != v) {
                    useSiblingsDeviceBank = v;
                    valueChanged();
                }
            });

        int siblingsDeviceBankNumDevicesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Pads", "SiblingsDeviceBank (needs CursorDevice)", siblingsDeviceBankNumDevices, INT_OPTIONS_2TO16, v -> {
                if (siblingsDeviceBankNumDevices != v) {
                    siblingsDeviceBankNumDevices = v;
                    valueChanged();
                }
            });
        
        // --> ChainDeviceBank
        SettableBooleanValue useChainDeviceBankValue = pref.getBooleanSetting
            ("Use", "ChainDeviceBank (needs CursorDevice)", useChainDeviceBank);
        useChainDeviceBankValue.addValueObserver(v -> {
                if (useChainDeviceBank != v) {
                    useChainDeviceBank = v;
                    valueChanged();
                }
            });

        int chainDeviceBankNumDevicesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Pads", "ChainDeviceBank (needs CursorDevice)", chainDeviceBankNumDevices, INT_OPTIONS_2TO16, v -> {
                if (chainDeviceBankNumDevices != v) {
                    chainDeviceBankNumDevices = v;
                    valueChanged();
                }
            });
        
        // --> SceneBank
        SettableBooleanValue useSceneBankValue = pref.getBooleanSetting
            ("Use", "SceneBank", useSceneBank);
        useSceneBankValue.addValueObserver(v -> {
                if (useSceneBank != v) {
                    useSceneBank = v;
                    valueChanged();
                }
            });

        int sceneBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "SceneBank", sceneBankNumScenes, INT_OPTIONS_4TO32, v -> {
                if (sceneBankNumScenes != v) {
                    sceneBankNumScenes = v;
                    valueChanged();
                }
            });

        // --> MainTrackBank
        SettableBooleanValue useMainTrackBankValue = pref.getBooleanSetting
            ("Use", "MainTrackBank", useMainTrackBank);
        useMainTrackBankValue.addValueObserver(v -> {
                if (useMainTrackBank != v) {
                    useMainTrackBank = v;
                    valueChanged();
                }
            });
        
        int mainTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "MainTrackBank", mainTrackBankNumTracks, INT_OPTIONS_4TO32, v -> {
                if (mainTrackBankNumTracks != v) {
                    mainTrackBankNumTracks = v;
                    valueChanged();
                }
            });

        int mainTrackBankNumSendsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Sends", "MainTrackBank", mainTrackBankNumSends, INT_OPTIONS_1TO8, v -> {
                if (mainTrackBankNumSends != v) {
                    mainTrackBankNumSends = v;
                    valueChanged();
                }
            });

        int mainTrackBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "MainTrackBank", mainTrackBankNumScenes, INT_OPTIONS_4TO32, v -> {
                if (mainTrackBankNumScenes != v) {
                    mainTrackBankNumScenes = v;
                    valueChanged();
                }
            });
        
        SettableBooleanValue mainTrackBankFollowCursorTrackValue = pref.getBooleanSetting
            ("Follow CursorTrack", "MainTrackBank", mainTrackBankFollowCursorTrack);
        useMainTrackBankValue.addValueObserver(v -> {
                if (mainTrackBankFollowCursorTrack != v) {
                    mainTrackBankFollowCursorTrack = v;
                    valueChanged();
                }
            });


        // --> EffectrackBank
        SettableBooleanValue useEffectTrackBankValue = pref.getBooleanSetting
            ("Use", "EffectTrackBank", useEffectTrackBank);
        useEffectTrackBankValue.addValueObserver(v -> {
                if (useEffectTrackBank != v) {
                    useEffectTrackBank = v;
                    valueChanged();
                }
            });

        int effectTrackBankNumTracksValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tracks", "EffectTrackBank", effectTrackBankNumTracks, INT_OPTIONS_1TO8, v -> {
                if (effectTrackBankNumTracks != v) {
                    effectTrackBankNumTracks = v;
                    valueChanged();
                }
            });

        int effectTrackBankNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "EffectTrackBank", effectTrackBankNumScenes, INT_OPTIONS_4TO32, v -> {
                if (effectTrackBankNumScenes != v) {
                    effectTrackBankNumScenes = v;
                    valueChanged();
                }
            });

        // --> MasterTrack
        SettableBooleanValue useMasterTrackValue = pref.getBooleanSetting
            ("Use", "MasterTrack", useMasterTrack);
        useMasterTrackValue.addValueObserver(v -> {
                if (useMasterTrack != v) {
                    useMasterTrack = v;
                    valueChanged();
                }
            });

        int masterTrackNumScenesValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Scenes", "MasterTrack", masterTrackNumScenes, INT_OPTIONS_4TO32, v -> {
                if (masterTrackNumScenes != v) {
                    masterTrackNumScenes = v;
                    valueChanged();
                }
            });

        // --> PopupBrowser
        SettableBooleanValue useBrowserValue = pref.getBooleanSetting
            ("Use", "PopupBrowser", useBrowser);
        useBrowserValue.addValueObserver(v -> {
                if (useBrowser != v) {
                    useBrowser = v;
                    valueChanged();
                }
            });

        int browserSmartCollectionRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Smart collection rows", "PopupBrowser", browserSmartCollectionRows, INT_OPTIONS_16TO128, v -> {
                if (browserSmartCollectionRows != v) {
                    browserSmartCollectionRows = v;
                    valueChanged();
                }
            });

        int browserLocationRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Location rows", "PopupBrowser", browserLocationRows, INT_OPTIONS_16TO128, v -> {
                if (browserLocationRows != v) {
                    browserLocationRows = v;
                    valueChanged();
                }
            });
        
        int browserDeviceRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Device rows", "PopupBrowser", browserDeviceRows, INT_OPTIONS_16TO128, v -> {
                if (browserDeviceRows != v) {
                    browserDeviceRows = v;
                    valueChanged();
                }
            });
        
        int browserCategoryRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Category rows", "PopupBrowser", browserCategoryRows, INT_OPTIONS_16TO128, v -> {
                if (browserCategoryRows != v) {
                    browserCategoryRows = v;
                    valueChanged();
                }
            });
        
        int browserTagRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Tag rows", "PopupBrowser", browserTagRows, INT_OPTIONS_16TO128, v -> {
                if (browserTagRows != v) {
                    browserTagRows = v;
                    valueChanged();
                }
            });
        
        int browserDeviceTypeRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Device type rows", "PopupBrowser", browserDeviceTypeRows, INT_OPTIONS_8TO64, v -> {
                if (browserDeviceTypeRows != v) {
                    browserDeviceTypeRows = v;
                    valueChanged();
                }
            });
        
        int browserFileTypeRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "File type rows", "PopupBrowser", browserFileTypeRows, INT_OPTIONS_8TO64, v -> {
                if (browserFileTypeRows != v) {
                    browserFileTypeRows = v;
                    valueChanged();
                }
            });
        
        int browserCreatorRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Creator rows", "PopupBrowser", browserCreatorRows, INT_OPTIONS_16TO128, v -> {
                if (browserCreatorRows != v) {
                    browserCreatorRows = v;
                    valueChanged();
                }
            });
        
        int browserResultsRowsValue = ExtensionUtils.getPreferenceAsIntOptions
            (pref, "Results rows", "PopupBrowser", browserResultsRows, INT_OPTIONS_16TO128, v -> {
                if (browserResultsRows != v) {
                    browserResultsRows = v;
                    valueChanged();
                }
            });
        
        // for future use
        if (!USE_RC_FILE) {
            webSocketPort = (int)webSocketPortValue.getRaw();
            rpcProtocol = protocol;
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

        host.getPreferences().getSignalSetting
            ("Apply new settings", "Restart (new settings need restart)", "Restart")
            .addSignalObserver(() -> host.restart());
        // TODO preference panel dosen't update at restat.
        host.getPreferences().getSignalSetting
            ("Reset to defaults", "Restart (new settings need restart)", "Restart")
            .addSignalObserver(() -> {
                    requestReset();
                    host.restart();
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onExit() {
    }
}
