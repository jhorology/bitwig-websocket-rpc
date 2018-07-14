package com.github.jhorology.bitwig.reflect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;
import com.github.jhorology.bitwig.extension.Logger;

public class MethodRegistry {
    private static final Logger log = Logger.getLogger(MethodRegistry.class);
    private static final MethodRegistry instance = new MethodRegistry();
    
    private Map<String, Object> modules;
    private Map<String, Multimap<String, Method>> methods;
    
    private MethodRegistry() {
    }

    public static MethodRegistry getInstance() {
        return instance;
    }
    
    @Subscribe
    public void onInit(InitEvent e) {
        // for debug
        modules = new HashMap<>();
        methods = new HashMap();
        register("test", new TestImpl(), Test.class);
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        if (modules != null) {
            modules.clear();
        }
        if (methods != null) {
            for(Multimap<String, Method> multimap : methods.values()) {
                multimap.clear();
            }
            methods.clear();
        }
    }

    public MethodInvoker getMethodInvoker(String name, int parameterCount) {
        int index = name.lastIndexOf('.');
        if (index < 1 || index > (name.length() - 1)) {
            log.warn("name should be formatted as \"[moduleName].[methodName]\".");
            return null;
        }
        String moduleName = name.substring(0, index);
        Object module = modules.get(moduleName);
        if (module == null) {
            log.warn("'" + moduleName + "' module not found.");
            return null;
        }
        String methodName = name.substring(index + 1);
        Multimap<String, Method> multimap = methods.get(moduleName);
        Collection<Method> sameNamedMethods = multimap.get(methodName);
        if (sameNamedMethods == null || sameNamedMethods.isEmpty()) {
            log.warn("'" + name + "' method not found.");
            return null;
        }
        for(Method method : sameNamedMethods) {
            if (method.getParameterCount() == parameterCount) {
                return new MethodInvoker(module, method);
            }
        }
        log.warn("'" + name + "' method with " + parameterCount + " parameters not found.");
        return null;
    }
    
    private void register(String moduleName, Object module, Class<?> interfaceType) {
        registerModule(moduleName, module);
        registerMethod(moduleName, interfaceType);
    }
    
    private void registerModule(String moduleName, Object module) {
        modules.put(moduleName, module);
    }
    
    private void registerMethod(String moduleName, Class<?> interfaceType) {
        Multimap<String, Method> multimap = methods.get(moduleName);
        if (multimap == null) {
            multimap = ArrayListMultimap.create();
            methods.put(moduleName, multimap);
        }
        for(Method method : interfaceType.getMethods()) {
            if(Modifier.isPublic(method.getModifiers())) {
                multimap.put(method.getName(), method);
            }
        }
    }
}
