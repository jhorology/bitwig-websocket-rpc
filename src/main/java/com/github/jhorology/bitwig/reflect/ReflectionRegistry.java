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
import java.util.HashMap;
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

    public ReflectionRegistry() {
        log = Logger.getLogger(ReflectionRegistry.class);
        modules = new ConcurrentHashMap<>();
        pushEventBus = new EventBus(this);
    }

    @Subscribe
    public void onInit(InitEvent e) {
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
     * <pre>{@code
     *   {
     *       "<modeulName>": {
     *         "methods": [
     *           "Number <modeulName>.test.sum(Number, Number)",
     *            ...
     *         ],
     *         "events": [
     *           "<modeulName>.test.event1",
     *         ],
     *       },
     *       ...
     *   }
     * }</pre>
     */
    @Override
    public Map<String, Map<String, List<Map<String,Object>>>> report() {
        return modules.keySet().stream()
            .sorted()
            .map(key -> modules.get(key))
            .collect(HashMap::new, (r, m) -> {
                    Map<String, List<Map<String, Object>>> module = new HashMap<>();
                    module.put("methods", m.getMethods()
                               .keySet().stream().sorted()
                               .map(key -> m.getMethods().get(key))
                               .map(m1 -> {
                                       Map<String, Object> method = new HashMap<>();
                                       method.put("method", m1.getAbsoluteName());
                                       method.put("params", m1.getRpcParamTypes()
                                                  .stream()
                                                  .map(t -> t.getExpression()).
                                                  collect(Collectors.toList()));
                                       method.put("return", m1.getRpcReturnType().getExpression());
                                       return method;
                                   })
                               .collect(Collectors.toList()));
                    module.put("events",  m.getEvents()
                               .keySet().stream().sorted()
                               .map(key -> m.getEvents().get(key))
                               .map(e -> {
                                       Map<String, Object> event = new HashMap<>();
                                       event.put("event", e.getAbsoluteName());
                                       return event;
                                   })
                               .collect(Collectors.toList()));
                    r.put(m.getModuleName(), module);
                }, Map::putAll);
    }

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
