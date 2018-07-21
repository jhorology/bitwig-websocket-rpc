package com.github.jhorology.bitwig.reflect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;

import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.test.Test;
import com.github.jhorology.bitwig.rpc.test.TestImpl;
import com.github.jhorology.bitwig.rpc.Rpc;
import com.github.jhorology.bitwig.rpc.RpcImpl;
import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;

public class MethodRegistry {
    private Logger log;

    private Map<String, ModuleHolder> modules;
    
    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(MethodRegistry.class);
        modules = new ConcurrentHashMap<>();
        try {
            // for test
            register("test", Test.class, new TestImpl());
            register("rpc", Rpc.class, new RpcImpl());
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
        int index = name.lastIndexOf('.');
        if (index < 1 || index > (name.length() - 1)) {
            log.warn("name should be formatted as \"[moduleName].[methodName]\".");
            return null;
        }
        String moduleName = name.substring(0, index);
        String methodName = name.substring(index + 1);
        ModuleHolder module = modules.get(moduleName);
        if (module == null) {
            log.warn("'" + moduleName + "' module not found.");
            return null;
        }
        MethodHolder method = module.getMethod(methodName, paramTypes);
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

    public void register(String moduleName, Class<?> interfaceType, Object module) throws IllegalAccessException {
        modules.put(moduleName, new ModuleHolder(moduleName, interfaceType, module));
    }
    
    public void register(String moduleName, Class<?> interfaceType) throws IllegalAccessException {
        modules.put(moduleName, new ModuleHolder(moduleName, interfaceType));
    }

    public void setModuleInstance(String moduleName, Object moduleInstance) {
        ModuleHolder module = modules.get(moduleName);
        if (module != null) {
            module.setModuleInstance(moduleInstance);
        }
    }
}
