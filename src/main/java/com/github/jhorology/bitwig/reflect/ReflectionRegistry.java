package com.github.jhorology.bitwig.reflect;

import com.bitwig.extension.controller.api.Transport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;
import com.github.jhorology.bitwig.rpc.Rpc;
import com.github.jhorology.bitwig.rpc.RpcImpl;
import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;

public class ReflectionRegistry {
    private Logger log;

    private Map<String, ModuleHolder<?>> modules;
    
    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(this.getClass());
        modules = new ConcurrentHashMap<>();
        try {
            // for test
            register("test", Test.class, new TestImpl());
            register("rpc", Rpc.class, new RpcImpl());
            register("transport", Transport.class, e.getExtension().getHost().createTransport());
        } catch (IllegalAccessException ex) {
            log.error(ex);
        }
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        if (modules != null) {
            for(ModuleHolder module : modules.values()) {
                module.clear();
            }
            modules.clear();
        }
    }

    public MethodHolder getMethod(String name, List<SloppyType> paramTypes) {
        ImmutablePair<String, String> pair = parseName(name);
        String moduleName = pair.getLeft();
        String methodName = pair.getRight();
        ModuleHolder module = getModule(moduleName);
        if (module == null) {
            return null;
        }
        MethodHolder method = module.getMethod(methodName, paramTypes);
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

    public EventHolder getEvent(String name) {
        ImmutablePair<String, String> pair = parseName(name);
        String moduleName = pair.getLeft();
        String eventName = pair.getRight();
        ModuleHolder module = getModule(moduleName);
        if (module == null) {
            return null;
        }
        return module.getEvent(eventName);
    }

    public <T> void register(String moduleName, Class<T> interfaceType, T module) throws IllegalAccessException {
        modules.put(moduleName, new ModuleHolder<>(moduleName, interfaceType, module));
    }
    
    public <T> void register(String moduleName, Class<T> interfaceType) throws IllegalAccessException {
        modules.put(moduleName, new ModuleHolder<>(moduleName, interfaceType));
    }

    public <T> void setModuleInstance(String moduleName, Class<T> interfaceType, T moduleInstance) {
        @SuppressWarnings("unchecked")
        ModuleHolder<T> module = (ModuleHolder<T>) modules.get(moduleName);
        if (module != null) {
            module.setModuleInstance(moduleInstance);
        }
    }

    private ModuleHolder getModule(String moduleName) {
        ModuleHolder module = modules.get(moduleName);
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
}
