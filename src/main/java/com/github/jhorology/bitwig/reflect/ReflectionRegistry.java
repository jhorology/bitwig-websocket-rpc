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

import com.bitwig.extension.Extension;
import com.bitwig.extension.ExtensionDefinition;
import com.bitwig.extension.api.Host;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.rpc.RpcRegistry;

public class ReflectionRegistry
    implements RpcRegistry,SubscriberExceptionHandler {

    private final Logger log;

    // server sent evnt bus.
    private final Map<String, ModuleHolder<?>> modules;
    private final EventBus pushEventBus;

    private Extension extension;
    
    public ReflectionRegistry() {
        log = Logger.getLogger(ReflectionRegistry.class);
        modules = new ConcurrentHashMap<>();
        pushEventBus = new EventBus(this);
    }

    @Subscribe
    public void onInit(InitEvent e) {
        extension = e.getExtension();
    }

    @Subscribe
    public void onExit(ExitEvent e) {
        if (modules != null) {
            modules.values().forEach(ModuleHolder::clear);
            modules.clear();
        }
    }

    @Override
    public RpcMethod getRpcMethod(String name, List<RpcParamType> paramTypes) {
        ImmutablePair<String, String> pair = parseName(name);
        if (pair == null) {
            return null;
        }
        String moduleName = pair.getLeft();
        String methodName = pair.getRight();
        ModuleHolder<?> module = getModule(moduleName);
        if (module == null) {
            return null;
        }
        RpcMethod method = module.getMethod(methodName, paramTypes);
        // try matching varargs
        if (method == null && paramTypes.size() >=1) {
            final RpcParamType expectedType = paramTypes.get(0);
            if(!expectedType.isArray()
               && paramTypes.stream()
               .allMatch(t -> (t == expectedType))) {
                RpcParamType arrayType = expectedType.toArrayType();
                method = module.getMethod(methodName, Arrays.asList(new RpcParamType[] {arrayType}));
            }
        }
        return method;
    }

    @Override
    public RpcEvent getRpcEvent(String name) {
        ImmutablePair<String, String> pair = parseName(name);
        String moduleName = pair.getLeft();
        String eventName = pair.getRight();
        ModuleHolder<?> module;
        module = getModule(moduleName);
        if (module == null) {
            return null;
        }
        return module.getEvent(eventName);
    }

    @Override
    public void subscribePushEvent(Object object) {
        pushEventBus.register(object);
    }

    @Override
    public void unsubscribePushEvent(Object object) {
        pushEventBus.unregister(object);
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
    
    /**
     * Handler for exceptions thrown by event subscribers.
     * @param exception
     * @param context
     */
    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        log.error( "push event handling error. event:" +  context.getEvent().toString(), exception);
    }
    
    public <T> void register(String moduleName, Class<T> interfaceType, T module)
        throws IllegalAccessException {
        modules.put(moduleName,
                    new ModuleHolder<>(moduleName, interfaceType, module, pushEventBus));
    }

    public <T> void register(String moduleName, Class<T> interfaceType)
        throws IllegalAccessException {
        modules.put(moduleName,
                    new ModuleHolder<>(moduleName, interfaceType, pushEventBus));
    }

    public <T> void setModuleInstance(String moduleName, Class<T> interfaceType, T moduleInstance) {
        @SuppressWarnings("unchecked")
            ModuleHolder<T> module = (ModuleHolder<T>) modules.get(moduleName);
        if (module != null) {
            module.setModuleInstance(moduleInstance);
        }
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
        List<Object> list = modules.keySet()
            .stream()
            .sorted()
            .map(key -> modules.get(key))
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

    private ImmutablePair<String, String> parseName(String name) {
        int index = name.indexOf('.');
        if (index < 1 || index > (name.length() - 1)) {
            log.warn("name should be formatted as \"[moduleName].[method or event name]\".");
            return null;
        }
        return new ImmutablePair<>(name.substring(0, index), name.substring(index + 1));
    }
}
