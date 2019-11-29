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
package com.github.jhorology.bitwig.reflect;

// jdk
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// bitwig api
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserFilterItem;
import com.bitwig.extension.controller.api.BrowserFilterItemBank;
import com.bitwig.extension.controller.api.BrowserResultsColumn;
import com.bitwig.extension.controller.api.BrowserResultsItem;
import com.bitwig.extension.controller.api.BrowserResultsItemBank;
import com.bitwig.extension.controller.api.ChainSelector;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ClipLauncherSlotOrSceneBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarkerBank;
import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceLayerBank;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.Groove;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.Mixer;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.PopupBrowser;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.Transport;

// provided dependencies
import com.google.common.eventbus.Subscribe;

// dependencies
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.rpc.Rpc;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcImpl;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;

/**
 *
 */
public class ReflectionRegistry implements RpcRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionRegistry.class);

    /**
     * the delimiter for method chain.
     */
    public static final String NODE_DELIMITER = ".";

    // server sent evnt bus.
    private final List<ModuleHolder> modules;
    private final Map<MethodIdentifier, MethodHolder> methods;
    private final Map<String, EventHolder> events;

    private final Config config;
    private final ProtocolHandler protocol;
    
    private ControllerHost host;
    private ControllerExtensionDefinition definition;

    /**
     * Constructor
     * @param config
     * @param protocol
     */
    public ReflectionRegistry(Config config, ProtocolHandler protocol) {
        this.config = config;
        this.protocol = protocol;
        modules = new ArrayList<>();
        methods = new LinkedHashMap<>(512);
        events = new LinkedHashMap<>(256);
    }

    // TODO Guava 19 or above EventBus is able to register non-public @﻿Subscribe

    @Subscribe
    public final void onInit(InitEvent<?> e) {
        host = e.getHost();
        definition = e.getDefinition();

        String id = definition.getId().toString();

        register("rpc",  Rpc.class, new RpcImpl());
        // for test
        register("test", Test.class, new TestImpl());

        register("host",
                 ControllerHost.class,
                 host);

        if (config.useApplication()) {
            Application application = host.createApplication();
            register("application",
                     Application.class,
                     application);
        }
        if (config.useTransport()) {
            Transport transport = host.createTransport();
            register("transport",
                     Transport.class,
                     transport);
        }
        if (config.useArranger()) {
            Arranger arranger = host.createArranger();
            register("arranger",
                     Arranger.class,
                     arranger);
            CueMarkerBank cueMarkerBank =
                arranger.createCueMarkerBank(config.getArrangerCueMakerSize());
            register("arranger.cueMarkerBank",
                     CueMarkerBank.class,
                     cueMarkerBank)
                .registerBankItemCount(CueMarkerBank.class,
                                       config.getArrangerCueMakerSize());
        }
        if (config.useGroove()) {
            Groove groove = host.createGroove();
            register("groove",
                     Groove.class,
                     groove);
        }
        if (config.useMixer()) {
            Mixer mixer = host.createMixer();
            register("mixer",
                     Mixer.class,
                     mixer);
        }
        if (config.useArrangerCursorClip()) {
            Clip arrangerCursorClip = host.createArrangerCursorClip(config.getArrangerCursorClipGridWidth(),
                                                                    config.getArrangerCursorClipGridHeight());
            register("arrangerCursorClip",
                     Clip.class,
                     arrangerCursorClip);
            if (LOG.isDebugEnabled()) {
                LOG.debug("arrangeCursorClip class:{}", arrangerCursorClip.getClass());
            }
        }
        if (config.useLauncherCursorClip()) {
            Clip launcherCursorClip = host.createLauncherCursorClip(config.getLauncherCursorClipGridWidth(),
                                                                    config.getLauncherCursorClipGridHeight());
            register("launcherCursorClip",
                     Clip.class,
                     launcherCursorClip);
            if (LOG.isDebugEnabled()) {
                LOG.debug("launcherCursorClip class:{}", launcherCursorClip.getClass());
            }
        }

        CursorTrack cursorTrack = null;
        if (config.useCursorTrack()) {
            cursorTrack =
                host.createCursorTrack(id, definition.getName(),
                                       config.getCursorTrackNumSends(),
                                       config.getCursorTrackNumScenes(),
                                       config.cursorTrackShouldFollowSelection());
            register("cursorTrack",
                     CursorTrack.class,
                     cursorTrack)
                .registerBankItemCount(SendBank.class,
                                       config.getCursorTrackNumSends())
                .registerBankItemCount(ClipLauncherSlotOrSceneBank.class,
                                       config.getCursorTrackNumScenes());
            if (config.useSiblingsTrackBank()) {
                TrackBank siblingsTrackBank =
                    cursorTrack.createSiblingsTrackBank(config.getSiblingsTrackBankNumTracks(),
                                                        config.getCursorTrackNumSends(),
                                                        config.getCursorTrackNumScenes(),
                                                        config.isSiblingsTrackBankIncludeEffectTracks(),
                                                        config.isSiblingsTrackBankIncludeMasterTrack());
                register("siblingsTrackBank",
                         TrackBank.class,
                         siblingsTrackBank)
                    .registerBankItemCount(TrackBank.class,
                                           config.getSiblingsTrackBankNumTracks())
                    .registerBankItemCount(SendBank.class,
                                           config.getCursorTrackNumSends())
                    .registerBankItemCount(ClipLauncherSlotOrSceneBank.class,
                                           config.getCursorTrackNumScenes());
            }
            if (config.useChildTrackBank()) {
                TrackBank childTrackBank =
                    cursorTrack.createTrackBank(config.getChildTrackBankNumTracks(),
                                                config.getCursorTrackNumSends(),
                                                config.getCursorTrackNumScenes(),
                                                config.isChildTrackBankHasFlatList());
                register("childTrackBank",
                         TrackBank.class,
                         childTrackBank)
                    .registerBankItemCount(TrackBank.class,
                                           config.getChildTrackBankNumTracks())
                    .registerBankItemCount(SendBank.class,
                                           config.getCursorTrackNumSends())
                    .registerBankItemCount(ClipLauncherSlotOrSceneBank.class,
                                           config.getCursorTrackNumScenes());
            }
            if (config.useCursorDevice()) {
                PinnableCursorDevice cursorDevice =
                    cursorTrack.createCursorDevice(id, definition.getName(),
                                                   config.getCursorDeviceNumSends(),
                                                   config.getCursorDeviceFollowMode());
                register("cursorDevice",
                         PinnableCursorDevice.class,
                         cursorDevice)
                    .registerBankItemCount(SendBank.class,
                                           config.getCursorDeviceNumSends());

                if (config.useChainSelector()) {
                    ChainSelector chainSelector = cursorDevice.createChainSelector();
                    register("chainSelector",
                             ChainSelector.class,
                             chainSelector)
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }

                if (config.useCursorDeviceLayer()) {
                    CursorDeviceLayer cursorDeviceLayer = cursorDevice.createCursorLayer();
                    register("cursorDeviceLayer",
                             CursorDeviceLayer.class,
                             cursorDeviceLayer)
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }

                if (config.useCursorRemoteControlsPage()) {
                    CursorRemoteControlsPage cursorRemoteControlsPage
                        = cursorDevice.createCursorRemoteControlsPage
                        (config.getCursorRemoteControlsPageParameterCount());
                    register("cursorRemoteControlsPage",
                             CursorRemoteControlsPage.class,
                             cursorRemoteControlsPage)
                        .registerBankItemCount(CursorRemoteControlsPage.class,
                                               config.getCursorRemoteControlsPageParameterCount());
                }

                if (config.useDeviceLayerBank()) {
                    DeviceLayerBank deviceLayerBank
                        = cursorDevice.createLayerBank(config.getDeviceLayerBankNumChannels());
                    register("deviceLayerBank",
                             DeviceLayerBank.class,
                             deviceLayerBank)
                        .registerBankItemCount(DeviceLayerBank.class,
                                               config.getDeviceLayerBankNumChannels())
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }
                if (config.useDrumPadBank()) {
                    DrumPadBank drumPadBank
                        = cursorDevice.createDrumPadBank(config.getDrumPadBankNumPads());
                    register("drumPadBank",
                             DrumPadBank.class,
                             drumPadBank)
                        .registerBankItemCount(DrumPadBank.class,
                                               config.getDrumPadBankNumPads())
                        .registerBankItemCount(SendBank.class,
                                               config.getCursorDeviceNumSends());
                }
                if (config.useSiblingsDeviceBank()) {
                    DeviceBank siblingsDeviceBank
                        = cursorDevice.createSiblingsDeviceBank(config.getSiblingsDeviceBankNumDevices());
                    register("siblingsDeviceBank",
                             DeviceBank.class,
                             siblingsDeviceBank)
                        .registerBankItemCount(DeviceBank.class,
                                               config.getSiblingsDeviceBankNumDevices());
                }
                if (config.useChainDeviceBank()) {
                    DeviceBank chainDeviceBank
                        = cursorDevice.deviceChain().createDeviceBank(config.getChainDeviceBankNumDevices());
                    register("chainDeviceBank",
                             DeviceBank.class,
                             chainDeviceBank)
                        .registerBankItemCount(DeviceBank.class,
                                               config.getChainDeviceBankNumDevices());
                }
            }
        }
        if (config.useSceneBank()) {
            SceneBank sceneBank
                = host.createSceneBank(config.getSceneBankNumScenes());
            register("sceneBank",
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
            register("mainTrackBank",
                     TrackBank.class,
                     mainTrackBank)
                .registerBankItemCount(TrackBank.class,
                                       config.getMainTrackBankNumTracks())
                .registerBankItemCount(SendBank.class,
                                       config.getMainTrackBankNumSends())
                .registerBankItemCount(ClipLauncherSlotOrSceneBank.class,
                                       config.getMainTrackBankNumScenes());
            if (config.useCursorTrack() && cursorTrack != null && config.isMainTrackBankFollowCursorTrack()) {
                mainTrackBank.followCursorTrack(cursorTrack);
            }
        }
        if (config.useEffectTrackBank()) {
            TrackBank effectTrackBank
                = host.createEffectTrackBank(config.getEffectTrackBankNumTracks(),
                                             config.getEffectTrackBankNumScenes());
            register("effectTrackBank",
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
            register("masterTrack",
                     MasterTrack.class,
                     masterTrack)
                .registerBankItemCount(ClipLauncherSlotBank.class,
                                       config.getMasterTrackNumScenes());
        }

        if (config.useBrowser()) {
            PopupBrowser popupBrowser = host.createPopupBrowser();
            register("browser",
                     PopupBrowser.class,
                     popupBrowser);
            registerBrowserFilterColumn(popupBrowser.smartCollectionColumn(),
                                        "smartCollectionColumn",
                                        config.getBrowserSmartCollectionRows());
            registerBrowserFilterColumn(popupBrowser.locationColumn(),
                                        "locationColumn",
                                        config.getBrowserLocationRows());
            registerBrowserFilterColumn(popupBrowser.deviceColumn(),
                                        "deviceColumn",
                                        config.getBrowserDeviceRows());
            registerBrowserFilterColumn(popupBrowser.categoryColumn(),
                                        "categoryColumn",
                                        config.getBrowserCategoryRows());
            registerBrowserFilterColumn(popupBrowser.tagColumn(),
                                        "tagColumn",
                                        config.getBrowserTagRows());
            registerBrowserFilterColumn(popupBrowser.deviceTypeColumn(),
                                        "deviceTypeColumn",
                                        config.getBrowserDeviceTypeRows());
            registerBrowserFilterColumn(popupBrowser.fileTypeColumn(),
                                        "fileTypeColumn",
                                        config.getBrowserFileTypeRows());
            registerBrowserFilterColumn(popupBrowser.creatorColumn(),
                                        "creatorColumn",
                                        config.getBrowserCreatorRows());
            registerBrowserResultsColumn(popupBrowser.resultsColumn(),
                                         config.getBrowserResultsRows());
        }
        //
        modules.forEach(m -> registerMethods(m));
    }

    @Subscribe
    public void onExit(ExitEvent<?> e) {
        modules.forEach(ModuleHolder::clear);
        modules.clear();
        methods.values().forEach(MethodHolder::clear);
        methods.clear();
        events.values().forEach(EventHolder::clear);
        events.clear();
    }

    /**
     * Get an interface for RPC method model.
     * @param name the method name.
     * @param paramTypes the parameter types
     * @return
     */
    @Override
    public RpcMethod getRpcMethod(String name, RpcParamType[] paramTypes) {
        RpcMethod method = methods.get(new MethodIdentifier(name, paramTypes));
        // try matching varargs
        if (method == null) {
            RpcParamType[] varargsTypes = ReflectUtils.toVarargs(paramTypes);
            if (varargsTypes != null) {
                method = methods.get(new MethodIdentifier(name, varargsTypes));
            }
        }
        return method;
    }

    /**
     * Get an interface for RPC event model.
     * @param name the event name.
     * @return
     */
    @Override
    public RpcEvent getRpcEvent(String name) {
        return events.get(name);
    }

    /**
     * clean up a client that has been disconnected.
     * @param client remote connection.
     */
    @Override
    public void disconnect(WebSocket client) {
        events.values().stream()
            .forEach(e -> e.disconnect(client));
    }

    /**
     * return a report object of this registry.
     * @return An object for expression of this registry.
     */
    @Override
    public Object report() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportedOn", new Date());
        report.put("host", reportHost());
        report.put("extension", reportExtension());
        //#if build.development
        report.put("system", System.getProperties());
        report.put("env", System.getenv());
        //#endif
        report.put("methods", reportMethods());
        report.put("events", reportEvents());
        return report;
    }

    /**
     * Register a RPC module.
     * @param moduleName     the name of module.
     * @param interfaceType  the type of interface.
     * @param moduleInstance the instance of interface.
     * @return the instance of ModuleHolder.
     */
    private ModuleHolder register(String moduleName, Class<?> interfaceType, Object moduleInstance) {
        ModuleHolder module = new ModuleHolder(config, moduleName, interfaceType, moduleInstance);
        modules.add(module);
        return module;
    }

    private void registerBrowserFilterColumn(BrowserFilterColumn column,
                                             String columnName,
                                             int itemSize) {
        // register cursor
        BrowserFilterItem cursorItem = column.createCursorItem();
        register("browser." + columnName + ".cursorItem",
                 BrowserFilterItem.class,
                 cursorItem);
        // register item bank
        BrowserFilterItemBank itemBank = column.createItemBank(itemSize);
        register("browser." + columnName + ".itemBank",
                 BrowserFilterItemBank.class,
                 itemBank)
            .registerBankItemCount(BrowserFilterItemBank.class,
                                   itemSize);
    }

    private void registerBrowserResultsColumn(BrowserResultsColumn column,
                                              int itemSize) {
        // register cursor
        BrowserResultsItem cursorItem = column.createCursorItem();
        register("browser.resultsColumn.cursorItem",
                 BrowserResultsItem.class,
                 cursorItem);
        // register item bank
        BrowserResultsItemBank itemBank = column.createItemBank(itemSize);
        register("browser.resultsColumn.itemBank",
                 BrowserResultsItemBank.class,
                 itemBank)
            .registerBankItemCount(BrowserResultsItemBank.class,
                                   itemSize);
    }

    /**
     * create a report object for Host.
     * @return
     */
    private Object reportHost() {
        Map<String,Object> report = new LinkedHashMap<>();
        report.put("apiVersion", host.getHostApiVersion());
        report.put("product", host.getHostProduct());
        report.put("vendor", host.getHostVendor());
        report.put("version", host.getHostVersion());
        report.put("platformType", host.getPlatformType().name());
        return report;
    }

    /**
     * create a report object for Extension.
     *
     * @return
     */
    private Object reportExtension() {
        Map<String,Object> report = new LinkedHashMap<>();
        report.put("name", definition.getName());
        report.put("author", definition.getAuthor());
        report.put("version", definition.getVersion());
        report.put("id", definition.getId().toString());
        report.put("requiredApiVersion", definition.getRequiredAPIVersion());
        report.put("hardwareVendor", definition.getHardwareVendor());
        report.put("hardwareModel", definition.getHardwareModel());
        report.put("usingBetaAPI", definition.isUsingBetaAPI());
        report.put("shouldFailOnDeprecatedUse", definition.shouldFailOnDeprecatedUse());
        return report;
    }

    /**
     * @return
     */
    private Object reportMethods() {
        List<Object> list = methods.values()
            .stream()
            .map(m-> m.reportRpcMethod())
            .collect(Collectors.toList());
        return list;
    }

    /**
     * @return
     */
    private Object reportEvents() {
        List<Object> list = events.values()
            .stream()
            .map(e-> e.reportRpcEvent())
            .collect(Collectors.toList());
        return list;
    }


    private void registerMethods(ModuleHolder module) {
        module.getMethodStream()
            .forEach(m -> registerMethod(module, m, module, 0));
    }

    private void registerMethod(ModuleHolder module, Method method, RegistryNode parentNode, int chainDepth) {
        // filter unusable methods
        Class<?> returnType = method.getReturnType();
        if (chainDepth > 5) {
            LOG.error("method:[{}.{}] chain depth is too long. Something is wrong !",
                      parentNode.getAbsoluteName(),
                      method.getName());
            return;
        }

        if (ReflectUtils.isBank(returnType) &&
            module.getBankItemCount(returnType) == 0) {
            // maybe correct
            // e.g) MasterTrack or EffctTrack has sendBank().getItemAt(int), but it can't be used.
            // TODO is need track.clipLauncherSlotBank or sendBank for Clip#getTrack ?
            if (LOG.isDebugEnabled()) {
                LOG.debug("method[{}.{}] bankItemCount is not registered !\n  {}",
                          parentNode.getAbsoluteName(),
                          method.getName(),
                          ReflectUtils.javaExpression(method));
            }
            return;
        }

        boolean isReturnTypeBitwigAPI = ReflectUtils.isBitwigAPI(returnType) || ReflectUtils.isExtAPI(returnType);
        boolean isReturnTypeBitwigValue = false;
        boolean isReturnTypeBitwigParameter = false;
        Class<?> bankItemType = null;
        int bankItemCount = 0;

        if (isReturnTypeBitwigAPI) {
            isReturnTypeBitwigValue = ReflectUtils.isBitwigValue(returnType);
            isReturnTypeBitwigParameter = ReflectUtils.isBitwigParameter(returnType);
            if (ReflectUtils.isBankMethod(method)) {
                bankItemType = ReflectUtils.getBankItemType(parentNode.getNodeType());
            }
            if (bankItemType != null) {
                // maybe returnType is ObjectProxy
                if (!bankItemType.isAssignableFrom(returnType)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("method[{}.{}] return-type isn't inherited bank item type.\n  expect:{}\n  occurs:{}",
                                  parentNode.getAbsoluteName(),
                                  method.getName(),
                                  bankItemType.getSimpleName(),
                                  returnType.getSimpleName());
                    }
                    // replace return type
                    returnType = bankItemType;
                }
                bankItemCount = module.getBankItemCount(parentNode.getNodeType());
            }
        }
        
        // if return type is implemented both Value and Parameter
        // shoud use Parameter#Value() as event.
        boolean isEvent = isReturnTypeBitwigValue &&
            ! isReturnTypeBitwigParameter;

        MethodHolder mh = isEvent
            ? new EventHolder(config,
                              method,
                              returnType,
                              parentNode,
                              bankItemCount,
                              host,
                              protocol.getPushModel())
            : new MethodHolder(config,
                               method,
                               returnType,
                               parentNode,
                               bankItemCount);

        if (! isReturnTypeBitwigAPI ||
            (isReturnTypeBitwigAPI &&
             protocol.isSerializableBitwigType(returnType))) {
            // for debug
            if (LOG.isWarnEnabled()) {
                MethodHolder duplicatedMethod = methods.get(mh.getIdentifier());
                if (duplicatedMethod != null) {
                    LOG.warn("method[{}} confilict with duplicated key !\n  old:{}\n  new:{}",
                             mh.getAbsoluteName(),
                             duplicatedMethod.getJavaExpression(),
                             mh.getJavaExpression());
                }
            }
            methods.put(mh.getIdentifier(), mh);
        }
        if (isEvent) {
            // for debug
            if (LOG.isWarnEnabled()) {
                EventHolder duplicatedEvent = events.get(mh.getAbsoluteName());
                if (duplicatedEvent != null) {
                    LOG.warn("event[{}] confilict with duplicated key !\n  old:{}\n  new:{}",
                             mh.getAbsoluteName(),
                             duplicatedEvent.getJavaExpression(),
                             mh.getJavaExpression());
                }
            }
            events.put(mh.getAbsoluteName(), (EventHolder)mh);
        }
        if (isReturnTypeBitwigAPI && !returnType.isEnum()) {
            // register method recursively
            mh.getMethodStream().forEach(m -> registerMethod(module, m, mh, chainDepth + 1));
        }
    }
}
