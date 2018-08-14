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
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Value;

// provided dependencies
import com.google.common.eventbus.Subscribe;

// dependencies
import org.java_websocket.WebSocket;

// source
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;

/**
 * 
 */
public class ReflectionRegistry implements RpcRegistry {
    /**
     * the delimitter for method chain.
     */
    public static final String NODE_DELIMITER = ".";
    private static final Logger LOG = Logger.getLogger(ReflectionRegistry.class);

    private final ProtocolHandler protocol;
    private final boolean useAbbrev;
    private ControllerHost host;
    private ControllerExtensionDefinition definition;
    
    // server sent evnt bus.
    private final List<ModuleHolder<?>> modules;
    private final Map<MethodIdentifier, MethodHolder<?>> methods;
    private final Map<String, EventHolder<?>> events;

    private boolean initialized;

    /**
     * 
     * @param protocol
     * @param useAbbrev 
     */
    public ReflectionRegistry(ProtocolHandler protocol, boolean useAbbrev) {
        this.protocol = protocol;
        this.useAbbrev = useAbbrev;
        modules = new ArrayList<>();
        methods = new LinkedHashMap<>(512);
        events = new LinkedHashMap<>(256);
    }

    @Subscribe
    public void onInit(InitEvent e) {
        host = e.getHost();
        definition = e.getExtension().getExtensionDefinition();
        modules.forEach(m -> registerMethods(m));
        
        initialized = true;
    }

    @Subscribe
    public void onExit(ExitEvent e) {
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
        report.put("system", System.getProperties());
        report.put("env", System.getenv());
        report.put("methods", reportMethods());
        report.put("events", reportEvents());
        return report;
    }

    /**
     * Register a RPC module.
     * @param <T>            the type of interface.
     * @param moduleName     the name of module.
     * @param interfaceType  the type of interface.
     * @param moduleInstance the instance of interface.
     * @return the instance of ModuleHolder.
     */
    public <T> ModuleHolder<T> register(String moduleName, Class<T> interfaceType, T moduleInstance) {
        ModuleHolder<T> module = new ModuleHolder<>(this,
                                                    useAbbrev ? AbbrevDict.abbrev(moduleName) : moduleName,
                                                    interfaceType,
                                                    moduleInstance);
        if (initialized) {
            throw new IllegalStateException("This method can be called only before initializtion.");
        }
        modules.add(module);
        return module;
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


    private void registerMethods(ModuleHolder<?> module) {
        ReflectUtils.getCleanMethods(module.getNodeType())
            .forEach(m -> registerMethod(module, m, module, 0));
    }
    
    @SuppressWarnings("unchecked")
    private void registerMethod(ModuleHolder<?> module, Method method, RegistryNode<?> parentNode, int chainDepth) {
        // filter unusable methods
        Class<?> returnType = method.getReturnType();
        String methodName = useAbbrev ? AbbrevDict.abbrev(method.getName()) : method.getName();
        String absoluteName = parentNode.getAbsoluteName() + NODE_DELIMITER + methodName;
        if (chainDepth > 5) {
            LOG.error("##!!! Method chain depth are too long. Something is wrong!!"
                      + "\nmethod:" + absoluteName);
            return;
        }
        
        if (ReflectUtils.isBank(returnType) &&
            module.getBankItemCount(returnType) == 0) {
            // maybe correct
            // e.g) MasterTrack or EffctTrack has sendBank().getItemAt(int), but it can't use.
            if (Logger.isDebugEnabled()) {
                LOG.debug("##!!! Bank type founded, but bankItemCount is not registered.!!"
                          + "\nmethod:" + absoluteName +
                          " bankType:" + returnType);
            }
            return;
        }
        
        boolean isReturnTypeBitwigAPI = ReflectUtils.isBitwigAPI(returnType);
        boolean isReturnTypeBitwigValue = false;
        boolean isReturnTypeBitwigParameter = false;
        Class<?> bankItemType = null;
        int bankItemCount = 0;
        List<Method> methodsOfReturnType = null;
        
        if (isReturnTypeBitwigAPI) {
            isReturnTypeBitwigValue = ReflectUtils.isBitwigValue(returnType);
            isReturnTypeBitwigParameter = ReflectUtils.isBitwigParameter(returnType);
            if (ReflectUtils.isBankMethod(method)) {
                bankItemType = ReflectUtils.getBankItemType(parentNode.getNodeType());
            }
            if (bankItemType != null) { 
                // maybe returnType is ObjectProxy
                if (!bankItemType.isAssignableFrom(returnType)) {
                    if (Logger.isDebugEnabled()) {
                        LOG.debug("##!!! Bank Method returns unassignable bank item type!!"
                                  + "\nmethod:" + absoluteName +
                                  " returnType:" + returnType +
                                  " replaceWith:" + bankItemType);
                    }
                    // replace return type
                    returnType = bankItemType;
                }
                bankItemCount = module.getBankItemCount(parentNode.getNodeType());
            }
            // correct conflicted methods
            methodsOfReturnType = ReflectUtils.getCleanMethods(returnType);
        }

        // if return type is implemented both Value and Parameter
        // shoud use Parameter#Value() as event.
        boolean isEvent = isReturnTypeBitwigValue &&
            ! isReturnTypeBitwigParameter;
        
        MethodHolder<?> mh = isEvent
            ? new EventHolder(methodName,
                              method,
                              (Class<? extends Value>)returnType,
                              parentNode,
                              bankItemCount,
                              host,
                              protocol.getPushModel())
            : new MethodHolder(methodName,
                               method,
                               returnType,
                               parentNode,
                               bankItemCount);
        
        if (! isReturnTypeBitwigAPI ||
            (isReturnTypeBitwigAPI &&
             protocol.isSerializableBitwigType(returnType))) {
            // for debug
            if (Logger.isDebugEnabled()) {
                MethodHolder<?> duplicatedMethod = methods.get(mh.getIdentifier());
                if (duplicatedMethod != null) {
                    LOG.debug("##!!! duplicated method!!"
                              + "\nevent:" + absoluteName
                              + "\nold:" + duplicatedMethod.getExpression(true)
                              + "\nnew:" + mh.getExpression(true));
                }
            }
            methods.put(mh.getIdentifier(), mh);
        }
        if (isEvent) {
            // for debug
            if (Logger.isDebugEnabled()) {
                EventHolder<?> duplicatedEvent = events.get(absoluteName);
                if (duplicatedEvent != null) {
                    LOG.warn("##!!! duplicated event!!"
                             + "\nevent:" + absoluteName
                             + "\nold:" + duplicatedEvent.parentNode.getNodeType().getSimpleName() + "#" + duplicatedEvent.getNodeName()
                             + "\nnew:" + mh.parentNode.getNodeType().getSimpleName() + "#" + mh.getNodeName());
                }
            }
            events.put(absoluteName, (EventHolder)mh);
        }
        // register method recursively
        if (methodsOfReturnType != null && !methodsOfReturnType.isEmpty()) {
            methodsOfReturnType
                .forEach(m -> registerMethod(module, m, mh, chainDepth + 1));
        }
    }
}
