package com.github.jhorology.bitwig.reflect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;

import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;

public class MethodRegistry {
    private Logger log;

    private Map<String, ModuleHolder> modules;
    
    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(MethodRegistry.class);
        modules = new ConcurrentHashMap<>();
        try {
            // for debug
            register("test", Test.class, Test.create());
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

    public MethodHolder getMethod(String name, int parameterCount) {
        int index = name.lastIndexOf('.');
        if (index < 1 || index > (name.length() - 1)) {
            log.warn("name should be formatted as \"[moduleName].[methodName]\".");
            return null;
        }
        String moduleName = name.substring(0, index);
        String methodName = name.substring(index + 1);
        return getMethod(moduleName, methodName, parameterCount);
    }
    
    public MethodHolder getMethod(String moduleName, String methodName, int parameterCount) {
        ModuleHolder module = modules.get(moduleName);
        if (module == null) {
            log.warn("'" + moduleName + "' module not found.");
            return null;
        }
        
        MethodHolder method = module.getMethodHolder(methodName, parameterCount);
        log.warn("'" + methodName + "' method with " + parameterCount + " parameters not found.");
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
