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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bitwig.extension.controller.api.Transport;

import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;
import com.github.jhorology.bitwig.rpc.Rpc;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcImpl;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;

public class ReflectionRegistry implements SubscriberExceptionHandler {
    private Logger log;

    // server sent evnt bus.
    private EventBus pushEventBus;
    private Map<String, ModuleHolder<?>> modules;
    
    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(this.getClass());
        modules = new ConcurrentHashMap<>();
        pushEventBus = new EventBus(this);
        try {
            // for test
            register("test", Test.class, new TestImpl());
            register("rpc", Rpc.class, new RpcImpl());
            register("transport", Transport.class, e.getHost().createTransport());
        } catch (IllegalAccessException ex) {
            log.error(ex);
        }
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        if (modules != null) {
            modules.values().forEach(ModuleHolder::clear);
            modules.clear();
        }
    }

    public RpcMethod getMethod(String name, List<SloppyType> paramTypes) {
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
            final SloppyType expectedType = paramTypes.get(0);
            if(!expectedType.isArray()
               && paramTypes.stream()
               .allMatch(t -> (t == expectedType))) {
                SloppyType arrayType = expectedType.toArrayType();
                method = module.getMethod(methodName, Arrays.asList(new SloppyType[] {arrayType}));
            }
        }
        return method;
    }

    public RpcEvent getEvent(String name) {
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

    
    public <T> void register(String moduleName, Class<T> interfaceType, T module) throws IllegalAccessException {
        modules.put(moduleName, new ModuleHolder<>(moduleName, interfaceType, module, pushEventBus));
    }
    
    public <T> void register(String moduleName, Class<T> interfaceType) throws IllegalAccessException {
        modules.put(moduleName, new ModuleHolder<>(moduleName, interfaceType, pushEventBus));
    }

    public <T> void setModuleInstance(String moduleName, Class<T> interfaceType, T moduleInstance) {
        @SuppressWarnings("unchecked")
        ModuleHolder<T> module = (ModuleHolder<T>) modules.get(moduleName);
        if (module != null) {
            module.setModuleInstance(moduleInstance);
        }
    }


    public void subscribeNotification(Object object) {
        pushEventBus.register(object);
    }

    public void unsubscribeNotification(Object object) {
        pushEventBus.unregister(object);
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
        int index = name.lastIndexOf('.');
        if (index < 1 || index > (name.length() - 1)) {
            log.warn("name should be formatted as \"[moduleName].[method or event name]\".");
            return null;
        }
        return new ImmutablePair<>(name.substring(0, index), name.substring(index + 1));
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        log.error( "notification event handling error. event:" +  context.getEvent().toString(), exception);
    }
}
