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
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;

// dependencies
import com.google.gson.annotations.Expose;

// source
import com.github.jhorology.bitwig.extension.AbstractConfiguration;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;
import com.github.jhorology.bitwig.ext.api.VuMeterUsedFor;
import com.github.jhorology.bitwig.ext.api.VuMeterChannelMode;
import com.github.jhorology.bitwig.ext.api.VuMeterPeakMode;

public class Config extends AbstractConfiguration {
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
    @Expose(serialize = false)
    private Protocols rpcProtocol = Protocols.JSONRPC20;
    @Expose
    private boolean useAbbreviatedMethodNames;
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
    private boolean useArrangerCursorClip;
    @Expose
    private int arrangerCursorClipGridWidth = 16;
    @Expose
    private int arrangerCursorClipGridHeight = 16;
    @Expose
    private boolean useLauncherCursorClip;
    @Expose
    private int launcherCursorClipGridWidth = 16;
    @Expose
    private int launcherCursorClipGridHeight = 16;
    @Expose
    private boolean useCursorTrack;
    @Expose
    private int cursorTrackNumSends = 2;
    @Expose
    private int cursorTrackNumScenes = 8;
    @Expose
    private boolean cursorTrackShouldFollowSelection;
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
    private boolean useCursorDeviceDirectParameter;
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
    private int browserSmartCollectionRows = 16;
    @Expose
    private int browserLocationRows = 32;
    @Expose
    private int browserDeviceRows = 32;
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
    @Expose
    private VuMeterUsedFor vuMeterUsedFor = VuMeterUsedFor.NONE;
    @Expose
    private int vuMeterRange = 32;
    @Expose
    private VuMeterChannelMode vuMeterChannelMode = VuMeterChannelMode.MONO;
    @Expose
    private VuMeterPeakMode vuMeterPeakMode = VuMeterPeakMode.RMS;
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
     * Returns a configuration value of the use or not use CursorDevice DirectParameter.
     * @return
     */
    public boolean useCursorDeviceDirectParameter() {
        return useCursorDeviceDirectParameter;
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
        return browserFileTypeRows;
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
     * Returns a configuration value of use VuMeter for
     * @return
     */
    public VuMeterUsedFor getVuMeterUsedFor() {
        return vuMeterUsedFor;
    }

    /**
     * Returns a configuration value of VuMeter channel mode
     * @return
     */
    public VuMeterChannelMode getVuMeterChannelMode() {
        return vuMeterChannelMode;
    }

    /**
     * Returns a configuration value of VuMeter channel mode
     * @return
     */
    public VuMeterPeakMode getVuMeterPeakMode() {
        return vuMeterPeakMode;
    }

    /**
     * Returns a configuration value of VuMeter range
     * @return
     */
    public int getVuMeterRange() {
        return vuMeterRange;
    }

    /**
     * Returns true if specified interface is needed VU Meter.
     * @param interfaceType
     * @return
     */
    public boolean useVuMeter(Class<?> interfaceType) {
        if (vuMeterUsedFor == VuMeterUsedFor.CURSOR_TRACK) {
            return CursorTrack.class.isAssignableFrom(interfaceType);
        }
        if (vuMeterUsedFor == VuMeterUsedFor.TRACK) {
            return Track.class.isAssignableFrom(interfaceType);
        }
        if (vuMeterUsedFor == VuMeterUsedFor.CHANNEL) {
            return Channel.class.isAssignableFrom(interfaceType);
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPrefItems() {
        addIntPrefItem("Server Port", WEBSOCKET_PREF_CATEGORY, 80, 9999, "",
                       this::getWebSocketPort,
                       v -> {webSocketPort = (int)v;});

        addEnumPrefItem("Protocol", WEBSOCKET_PREF_CATEGORY,
                        v -> v.getDisplayName(),
                        this::getRpcProtocol,
                        v -> {rpcProtocol = v;});

        //#if build.development
        addBoolPrefItem("Use abbreviated method names", WEBSOCKET_PREF_CATEGORY,
                        this::useAbbreviatedMethodNames,
                        v -> {useAbbreviatedMethodNames = v;});


        // --> Application
        addBoolPrefItem("Use", "Application",
                        this::useApplication,
                        v -> {useApplication = v;});

        // --> Transport
        addBoolPrefItem("Use", "Transport",
                        this::useTransport,
                        v -> {useTransport = v;});

        // --> Arranger
        addBoolPrefItem("Use", "Arranger",
                        this::useArranger,
                        v -> {useArranger = v;});
        addIntPrefItem("cue markers", "Arranger", INT_OPTIONS_8TO64,
                       this::getArrangerCueMakerSize,
                       v -> {arrangerCueMarkerSize = v;});

        // --> Groove
        addBoolPrefItem("Use", "Groove",
                        this::useGroove,
                        v -> {useGroove = v;});

        // --> Mixer
        addBoolPrefItem("Use", "Mixer",
                        this::useMixer,
                        v -> {useMixer = v;});

        // --> ArrangerCursorClip
        addBoolPrefItem("Use", "ArrangerCursorClip",
                        this::useArrangerCursorClip,
                        v -> {useArrangerCursorClip = v;});
        addIntPrefItem("Grid Width", "ArrangerCursorClip", INT_OPTIONS_8TO64,
                       this::getArrangerCursorClipGridWidth,
                       v -> {arrangerCursorClipGridWidth = v;});
        addIntPrefItem("Grid Height", "ArrangerCursorClip", INT_OPTIONS_8TO64,
                       this::getArrangerCursorClipGridHeight,
                       v -> {arrangerCursorClipGridHeight = v;});

        // --> LauncherCursorClip
        addBoolPrefItem("Use", "LauncherCursorClip",
                        this::useLauncherCursorClip,
                        v -> {useLauncherCursorClip = v;});
        addIntPrefItem("Grid Width", "LauncherCursorClip", INT_OPTIONS_8TO64,
                       this::getLauncherCursorClipGridWidth,
                       v -> {launcherCursorClipGridWidth = v;});
        addIntPrefItem("Grid Height", "LauncherCursorClip", INT_OPTIONS_8TO64,
                       this::getLauncherCursorClipGridHeight,
                       v -> {launcherCursorClipGridHeight = v;});

        // --> CursorTrack
        addBoolPrefItem("Use", "CursorTrack",
                        this::useCursorTrack,
                        v -> {useCursorTrack = v;});
        addIntPrefItem("Sends", "CursorTrack", INT_OPTIONS_1TO8,
                       this::getCursorTrackNumSends,
                       v -> {cursorTrackNumSends = v;});
        addIntPrefItem("Scenes", "CursorTrack", INT_OPTIONS_4TO32,
                       this::getCursorTrackNumScenes,
                       v -> {cursorTrackNumScenes = v;});
        addBoolPrefItem("Should follow selection", "CursorTrack",
                        this::cursorTrackShouldFollowSelection,
                        v -> {cursorTrackShouldFollowSelection = v;});

        // --> SiblingsTrackBank
        addBoolPrefItem("Use", "SiblingsTrackBank (needs CursorTrack)",
                        this::useSiblingsTrackBank,
                        v -> {useSiblingsTrackBank = v;});
        addIntPrefItem("Tracks", "SiblingsTrackBank (needs CursorTrack)", INT_OPTIONS_4TO32,
                       this::getSiblingsTrackBankNumTracks,
                       v -> {siblingsTrackBankNumTracks = v;});
        addBoolPrefItem("Include effect tracks", "SiblingsTrackBank (needs CursorTrack)",
                        this::isSiblingsTrackBankIncludeEffectTracks,
                        v -> {siblingsTrackBankIncludeEffectTracks = v;});
        addBoolPrefItem("Include master track", "SiblingsTrackBank (needs CursorTrack)",
                        this::isSiblingsTrackBankIncludeMasterTrack,
                        v -> {siblingsTrackBankIncludeMasterTrack = v;});

        // --> SiblingsTrackBank
        addBoolPrefItem("Use", "ChildTrackBank (needs CursorTrack)",
                        this::useChildTrackBank,
                        v -> {useChildTrackBank = v;});
        addIntPrefItem("Tracks", "ChildTrackBank (needs CursorTrack)", INT_OPTIONS_4TO32,
                       this::getChildTrackBankNumTracks,
                       v -> {childTrackBankNumTracks = v;});
        addBoolPrefItem("Has flat track list", "ChildTrackBank (needs CursorTrack)",
                        this::isChildTrackBankHasFlatList,
                        v -> {childTrackBankHasFlatList = v;});

        // --> CursorDevice
        addBoolPrefItem("Use", "CursorDevice (needs CursorTrack)",
                        this::useCursorDevice,
                        v -> {useCursorDevice = v;});
        addIntPrefItem("sends", "CursorDevice (needs CursorTrack)", INT_OPTIONS_1TO8,
                       this::getCursorDeviceNumSends,
                       v -> {cursorDeviceNumSends = v;});
        addEnumPrefItem("Follow mode", "CursorDevice (needs CursorTrack)",
                        this::getCursorDeviceFollowMode,
                        v -> {cursorDeviceFollowMode = v;});
        addBoolPrefItem("Use DirectParameter", "CursorDevice (needs CursorTrack)",
                        this::useCursorDeviceDirectParameter,
                        v -> {useCursorDeviceDirectParameter = v;});

        // --> ChainSelector
        addBoolPrefItem("Use", "ChainSelector (needs CursorDevice)",
                        this::useChainSelector,
                        v -> {useChainSelector = v;});

        // --> CursorDeviceLayer
        addBoolPrefItem("Use", "CursorDeviceLayer (needs CursorDevice)",
                        this::useCursorDeviceLayer,
                        v -> {useCursorDeviceLayer = v;});

        // --> CursorRemoteControlPage
        addBoolPrefItem("Use", "CursorRemoteControlPage (needs CursorDevice)",
                        this::useCursorRemoteControlsPage,
                        v -> {useCursorRemoteControlsPage = v;});
        addIntPrefItem("Controls", "CursorRemoteControlPage (needs CursorDevice)", INT_OPTIONS_4TO32,
                       this::getCursorRemoteControlsPageParameterCount,
                       v -> {cursorRemoteControlsPageParameterCount = v;});

        // --> DeviceLayerBank
        addBoolPrefItem("Use", "DeviceLayerBank (needs CursorDevice)",
                        this::useDeviceLayerBank,
                        v -> {useDeviceLayerBank = v;});
        addIntPrefItem("Channels", "DeviceLayerBank (needs CursorDevice)", INT_OPTIONS_4TO32,
                       this::getDeviceLayerBankNumChannels,
                       v -> {deviceLayerBankNumChannels = v;});

        // --> DrumPadBank
        addBoolPrefItem("Use", "DrumPadBank (needs CursorDevice)",
                        this::useDrumPadBank,
                        v -> {useDrumPadBank = v;});
        addIntPrefItem("Pads", "DrumPadBank (needs CursorDevice)", INT_OPTIONS_8TO64,
                       this::getDrumPadBankNumPads,
                       v -> {drumPadBankNumPads = v;});

        // --> SiblingsDeviceBank
        addBoolPrefItem("Use", "SiblingsDeviceBank (needs CursorDevice)",
                        this::useSiblingsDeviceBank,
                        v -> {useSiblingsDeviceBank = v;});
        addIntPrefItem("Devices", "SiblingsDeviceBank (needs CursorDevice)", INT_OPTIONS_2TO16,
                       this::getSiblingsDeviceBankNumDevices,
                       v -> {siblingsDeviceBankNumDevices = v;});

        // --> ChainDeviceBank
        addBoolPrefItem("Use", "ChainDeviceBank (needs CursorDevice)",
                        this::useChainDeviceBank,
                        v -> {useChainDeviceBank = v;});
        addIntPrefItem("Devices", "ChainDeviceBank (needs CursorDevice)", INT_OPTIONS_2TO16,
                       this::getChainDeviceBankNumDevices,
                       v -> {chainDeviceBankNumDevices = v;});

        // --> SceneBank
        addBoolPrefItem("Use", "SceneBank",
                        this::useSceneBank,
                        v -> {useSceneBank = v;});
        addIntPrefItem("Scenes", "SceneBank", INT_OPTIONS_4TO32,
                       this::getSceneBankNumScenes,
                       v -> {sceneBankNumScenes = v;});

        // --> MainTrackBank
        addBoolPrefItem("Use", "MainTrackBank",
                        this::useMainTrackBank,
                        v -> {useMainTrackBank = v;});
        addIntPrefItem("Tracks", "MainTrackBank", INT_OPTIONS_4TO32,
                       this::getMainTrackBankNumTracks,
                       v -> {mainTrackBankNumTracks = v;});
        addIntPrefItem("Sends", "MainTrackBank", INT_OPTIONS_1TO8,
                       this::getMainTrackBankNumSends,
                       v -> {mainTrackBankNumSends = v;});
        addIntPrefItem("Scenes", "MainTrackBank", INT_OPTIONS_4TO32,
                       this::getMainTrackBankNumScenes,
                       v -> {mainTrackBankNumScenes = v;});
        addBoolPrefItem("Follow CursorTrack", "MainTrackBank",
                        this::isMainTrackBankFollowCursorTrack,
                        v -> {mainTrackBankFollowCursorTrack = v;});

        // --> EffectrackBank
        addBoolPrefItem("Use", "EffectTrackBank",
                        this::useEffectTrackBank,
                        v -> {useEffectTrackBank = v;});
        addIntPrefItem("Tracks", "EffectTrackBank", INT_OPTIONS_1TO8,
                       this::getEffectTrackBankNumTracks,
                       v -> {effectTrackBankNumTracks = v;});
        addIntPrefItem("Scenes", "EffectTrackBank", INT_OPTIONS_4TO32,
                       this::getEffectTrackBankNumScenes,
                       v -> {effectTrackBankNumScenes = v;});

        // --> MasterTrack
        addBoolPrefItem("Use", "MasterTrack",
                        this::useMasterTrack,
                        v -> {useMasterTrack = v;});
        addIntPrefItem("Scenes", "MasterTrack", INT_OPTIONS_4TO32,
                       this::getMasterTrackNumScenes,
                       v -> {masterTrackNumScenes = v;});

        // --> PopupBrowser
        addBoolPrefItem("Use", "PopupBrowser",
                        this::useBrowser,
                        v -> {useBrowser = v;});
        addIntPrefItem("Smart collection rows", "PopupBrowser", INT_OPTIONS_8TO64,
                       this::getBrowserSmartCollectionRows,
                       v -> {browserSmartCollectionRows = v;});
        addIntPrefItem("Location rows", "PopupBrowser", INT_OPTIONS_16TO128,
                       this::getBrowserLocationRows,
                       v -> {browserLocationRows = v;});
        addIntPrefItem("Device rows", "PopupBrowser", INT_OPTIONS_16TO128,
                       this::getBrowserDeviceRows,
                       v -> {browserDeviceRows = v;});
        addIntPrefItem("Category rows", "PopupBrowser", INT_OPTIONS_16TO128,
                       this::getBrowserCategoryRows,
                       v -> {browserCategoryRows = v;});
        addIntPrefItem("Tag rows", "PopupBrowser", INT_OPTIONS_16TO128,
                       this::getBrowserTagRows,
                       v -> {browserTagRows = v;});
        addIntPrefItem("Device type rows", "PopupBrowser", INT_OPTIONS_8TO64,
                       this::getBrowserDeviceTypeRows,
                       v -> {browserDeviceTypeRows = v;});
        addIntPrefItem("File type rows", "PopupBrowser", INT_OPTIONS_8TO64,
                       this::getBrowserFileTypeRows,
                       v -> {browserFileTypeRows = v;});
        addIntPrefItem("Creator rows", "PopupBrowser", INT_OPTIONS_16TO128,
                       this::getBrowserCreatorRows,
                       v -> {browserCreatorRows = v;});
        addIntPrefItem("Result rows", "PopupBrowser", INT_OPTIONS_16TO128,
                       this::getBrowserResultsRows,
                       v -> {this.browserResultsRows = v;});

        // --> VU Meter
        addEnumPrefItem("Used for", "VU Meter",
                        e -> e.getDisplayValue(),
                        this::getVuMeterUsedFor,
                        v -> {vuMeterUsedFor = v;});
        addIntPrefItem("Range", "VU Meter", INT_OPTIONS_16TO128,
                       this::getVuMeterRange,
                       v -> {vuMeterRange = v;});
        addEnumPrefItem("Channel mode", "VU Meter",
                        e -> e.getDisplayValue(),
                        this::getVuMeterChannelMode,
                        v -> {vuMeterChannelMode = v;});
        addEnumPrefItem("Peak mode", "VU Meter",
                        e -> e.getDisplayValue(),
                        this::getVuMeterPeakMode,
                        v -> {vuMeterPeakMode = v;});
        //#endif
    }
}
