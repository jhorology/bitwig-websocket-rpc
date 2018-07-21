package com.github.jhorology.bitwig.reflect;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;

public class ModuleHolder<T> {
    private final Map<MethodIdentifier, MethodHolder> methods;
    private final Map<String, EventHolder> events;
    private T moduleInstance;
    
    ModuleHolder(String moduleName, Class<T> interfaceType) {
        methods = Arrays.stream(interfaceType.getMethods())
            .map(m -> new MethodHolder(this, m))
            .collect(Collectors.toMap(MethodHolder::getIdentifier, m -> m));
        // TODO temporary
        events = new HashMap<>();
    }
    
    ModuleHolder(String moduleName, Class<T> interfaceType, T moduleInstance) {
        this(moduleName, interfaceType);
        this.moduleInstance = moduleInstance;
    }

    public MethodHolder getMethod(String name, List<SloppyType> paramTypes) {
        return methods.get(new MethodIdentifier(name, paramTypes));
    }

    public EventHolder getEvent(String name) {
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
    }
}
