package com.github.jhorology.bitwig.reflect;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;
import com.google.common.eventbus.EventBus;
import java.lang.reflect.Method;

class ModuleHolder<T> {
    /**
     * the name of this module.
     */
    protected final String moduleName;
    
    /**
     * the interface typeof this module.
     */
    protected final Class<T> interfaceType;
    
    /**
     * the instance of interface of this module.
     */
    protected T moduleInstance;

    protected final EventBus pushEventBus;
    
    protected final Map<MethodIdentifier, MethodHolder> methods;
    protected final Map<String, EventHolder> events;
    
    /**
     * Constructor
     * @param moduleName
     * @param interfaceType
     */
    ModuleHolder(String moduleName, Class<T> interfaceType, EventBus pushEventBus) {
        this(moduleName, interfaceType, null, pushEventBus);
    }
    
    /**
     * Constructor
     * @param moduleName
     * @param interfaceType
     * @param moduleInstance
     */
    ModuleHolder(String moduleName, Class<T> interfaceType, T moduleInstance, EventBus pushEventBus) {
        this.moduleName = moduleName;
        this.interfaceType = interfaceType;
        this.events = new HashMap<>();
        this.methods = new HashMap<>();
        this.moduleInstance = moduleInstance;
        this.pushEventBus = pushEventBus;
        Arrays.stream(interfaceType.getMethods())
            .forEach(m -> registerMethod(m));
    }

    String getModuleName() {
        return moduleName;
    }
    

    MethodHolder getMethod(String name, List<SloppyType> paramTypes) {
        return methods.get(new MethodIdentifier(name, paramTypes));
    }

    EventHolder getEvent(String name) {
        return events.get(name);
    }
        
    Object getModuleInstance() {
        return moduleInstance;
    }
    
    void setModuleInstance(T moduleInstance) {
        this.moduleInstance = moduleInstance;
    }
    
    void clear() {
        methods.clear();
        events.clear();
    }

    protected void registerMethod(Method method) {
        registerMethod(method, "", null);
    }
    
    private void registerMethod(Method method, String prefix, MethodHolder parentChain) {
        // exclude addXxxxxObserver methods
        if (ReflectUtils.hasAnyCallbackParameter(method)) {
            return;
        }
        // method's return type is implemented the interfaces of both Subscribable and Value.
        boolean isEvent = ReflectUtils.isBitwigEvent(method);
        MethodHolder mh = isEvent
            ? new EventHolder(this, method, parentChain, pushEventBus)
            : new MethodHolder(this, method, parentChain);
        String key = prefix + method.getName();
        methods.put(mh.getIdentifier(), mh);
        if (isEvent) {
            events.put(key, (EventHolder)mh);
        }
        Class<?> returnType = method.getReturnType();
        // register method recursively
        if (ReflectUtils.isBitwigAPI(returnType)) {
            Method[] methods = returnType.getMethods();
            if (methods.length == 0) {
                return;
            }
            // TODO
            if (mh.getParamTypes().length != 0) {
                throw new UnsupportedOperationException("Not support Yet. '" + key + "' method that positions other than terminal end of chain has parameter(s).");
            }
            for(Method m : methods) {
                registerMethod(m, key + ".", mh);
            }
        }
    }
}
