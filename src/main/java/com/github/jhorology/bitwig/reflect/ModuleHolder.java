package com.github.jhorology.bitwig.reflect;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;

import com.google.common.eventbus.EventBus;

import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

class ModuleHolder<T> {
    // Logger
    private static final Logger LOG = Logger.getLogger(ModuleHolder.class);
    
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
    ModuleHolder(String moduleName, Class<T> interfaceType, T moduleInstance,
            EventBus pushEventBus) {
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
    
    Map<MethodIdentifier, MethodHolder> getMethods() {
        return methods;
    }
    
    Map<String, EventHolder> getEvents() {
        return events;
    }

    MethodHolder getMethod(String name, List<RpcParamType> paramTypes) {
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
    
    /**
     * cleanup this instance.
     */
    void clear() {
        events.values().stream().forEach(EventHolder::clear);
        events.clear();
        methods.values().stream().forEach(MethodHolder::clear);
        methods.clear();
    }

    /**
     * create a report object for this class.
     * @return 
     */
    Object report() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("name", getModuleName());
        report.put("methods", reportMethods());
        report.put("events", reportEvents());
        return report;
    }
    
    /**
     * create a report object for list of methods of this class.
     * @return 
     */
    private List<Object> reportMethods() {
        List<Object> list = methods.keySet()
            .stream()
            .sorted()
            .map(key -> methods.get(key))
            .map(mh -> mh.report())
            .collect(Collectors.toList());
       return list;
    }
    
    /**
     * create a report object for list of events of ModuleHolder.
     * @return 
     */
    private List<Object> reportEvents() {
        List<Object> list = events.keySet()
            .stream()
            .sorted()
            .map(key -> events.get(key))
            .map(eh -> eh.report())
            .collect(Collectors.toList());
        return list;
    }

    
    protected void registerMethod(Method method) {
        registerMethod(method, "", null);
    }
    
    private void registerMethod(Method method, String prefix, MethodHolder parentChain) {
        // exclude addXxxxxObserver methods
        if (ReflectUtils.hasAnyCallbackParameter(method)) {
            if (false && Logger.isWarnEnabled()) {
                LOG.warn("Ignore registering [" + moduleName + "."
                         + prefix + method.getName() + "] method that has callback paramater.");
            }
            return;
        }
        if (ReflectUtils.isDeprecated(method)) {
            if (false && Logger.isWarnEnabled()) {
                LOG.warn("Ignore registering deprecated [" + moduleName + "."
                         + prefix + method.getName() + "] method.");
            }
            return;
        }
        // method's return type is inherited from Value interface.
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
                if (Logger.isWarnEnabled()) {
                    LOG.warn("Failed to register [" + moduleName
                             + "." + key + "] method. The method that positions middle of chain could not have parameters." );
                }
            }
            for(Method m : methods) {
                registerMethod(m, key + ".", mh);
            }
        }
    }
}
