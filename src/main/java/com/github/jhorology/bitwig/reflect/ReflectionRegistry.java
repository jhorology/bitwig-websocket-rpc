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

// jvm
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// bitwig api
import com.bitwig.extension.ExtensionDefinition;
import com.bitwig.extension.api.Host;
import com.bitwig.extension.controller.api.ControllerHost;

// provided dependencies
import com.google.common.eventbus.Subscribe;

// dependencies
import org.java_websocket.WebSocket;

// source
import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.rpc.RpcRegistry;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;

public class ReflectionRegistry implements RpcRegistry {

    private Logger log;

    // server sent evnt bus.
    private final List<ModuleHolder<?>> preInitModules;
    private Map<String, ModuleHolder<?>> modules;

    private PushModel pushModel;
    private AbstractExtension extension;
    private boolean initialized;

    public ReflectionRegistry() {
        preInitModules = new ArrayList<>();
    }

    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(ReflectionRegistry.class);
        extension = e.getExtension();
        modules = new LinkedHashMap<>();
        initialized = true;
        preInitModules.stream().forEach(m -> {
                m.init();
                modules.put(m.getModuleName(), m);
            });
        preInitModules.clear();
    }

    @Subscribe
    public void onExit(ExitEvent e) {
        preInitModules.clear();
        if (modules != null) {
            modules.values().forEach(ModuleHolder::clear);
            modules.clear();
        }
    }

    /**
     * Register a interface of server-sent push model to use for trigger event.
     * @param pushModel
     */
    @Override
    public void registerPushModel(PushModel pushModel) {
        // should manage as List
        this.pushModel = pushModel;
    }

    /**
     * Get an interface for RPC method model.
     * @param name the method name.
     * @param paramTypes the parameter types
     * @return
     */
    @Override
    public RpcMethod getRpcMethod(String name, RpcParamType[] paramTypes) {
        String[] pair = parseName(name);
        if (pair == null) {
            return null;
        }
        String moduleName = pair[0];
        String methodName = pair[1];
        ModuleHolder<?> module = getModule(moduleName);
        if (module == null) {
            return null;
        }
        RpcMethod method = module.getMethod(methodName, paramTypes);
        // try matching varargs
        if (method == null) {
            RpcParamType[] varargsTypes = ReflectUtils.toVarargs(paramTypes);
            if (varargsTypes != null) {
                method = module.getMethod(methodName, varargsTypes);
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
        String[] pair = parseName(name);
        String moduleName = pair[0];
        String eventName = pair[1];
        ModuleHolder<?> module;
        module = getModule(moduleName);
        if (module == null) {
            return null;
        }
        return module.getEvent(eventName);
    }

    /**
     * clean up a client that has been disconnected.
     * @param client remote connextion.
     */
    @Override
    public void disconnect(WebSocket client) {
        modules.values().stream()
            .forEach(m -> m.disconnect(client));
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
        report.put("modules", reportModules());
        return report;
    }

    public <T> void register(String moduleName, Class<T> interfaceType, T target) {
        ModuleHolder<T> module = new ModuleHolder<>(this, moduleName, interfaceType, target);
        if (!initialized) {
            preInitModules.add(module);
        } else {
            module.init();
            modules.put(moduleName, module);
        }
    }

    public <T> void register(String moduleName, Class<T> interfaceType) {
        register(moduleName, interfaceType, null);
    }

    @SuppressWarnings("unchecked")
    public <T> void setModuleInstance(String moduleName, Class<T> interfaceType, T moduleInstance) {
        ModuleHolder<T> module = (ModuleHolder<T>) modules.get(moduleName);
        if (module != null) {
            module.setModuleInstance(moduleInstance);
        }
    }

    /**
     * Get a interface of server-sent push model for triger event.
     * @return
     */
    PushModel getPushModel() {
        return pushModel;
    }
    
    /**
     * Get an interface of ControllerHost.
     * @return
     */
    ControllerHost getHost() {
        return extension.getHost();
    }

    /**
     * create a report object for Host.
     * @return
     */
    private Object reportHost() {
        Map<String,Object> report = new LinkedHashMap<>();
        Host host = extension.getHost();
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
        ExtensionDefinition def = extension.getExtensionDefinition();
        report.put("name", def.getName());
        report.put("author", def.getAuthor());
        report.put("version", def.getVersion());
        report.put("requiredApiVersion", def.getRequiredAPIVersion());
        return report;
    }

    /**
     * create a report object for list of modules of this class.
     * @return
     */
    private Object reportModules() {
        List<Object> list = modules.values()
            .stream()
            .map(m-> m.report())
            .collect(Collectors.toList());
        return list;
    }

    private ModuleHolder<?> getModule(String moduleName) {
        ModuleHolder<?> module = modules.get(moduleName);
        if (module == null) {
            log.warn("'" + moduleName + "' module not found.");
            return null;
        }
        return module;
    }

    private String[] parseName(String name) {
        int index = name.indexOf('.');
        if (index < 1 || index > (name.length() - 1)) {
            log.warn("name should be formatted as \"[moduleName].[method or event name]\".");
            return null;
        }
        return new String[] {
            name.substring(0, index),
            name.substring(index + 1)
        };
    }
}
