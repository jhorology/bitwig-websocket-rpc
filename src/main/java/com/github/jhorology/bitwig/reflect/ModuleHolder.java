package com.github.jhorology.bitwig.reflect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;

public class ModuleHolder {
    private final Map<MethodIdentifier, MethodHolder> methods;
    private Object moduleInstance;
    
    ModuleHolder(String moduleName, Class<?> interfaceType) {
        methods = Arrays.stream(interfaceType.getMethods())
            .map(m -> new MethodHolder(this, m))
            .collect(Collectors.toMap(MethodHolder::getIdentifier, m -> m));
    }
    
    ModuleHolder(String moduleName, Class<?> interfaceType, Object moduleInstance) {
        this(moduleName, interfaceType);
        this.moduleInstance = moduleInstance;
    }

    public MethodHolder getMethod(String name, List<SloppyType> paramTypes) {
        return methods.get(new MethodIdentifier(name, paramTypes));
    }
        
    Object getModuleInstance() {
        return moduleInstance;
    }
    
    void setModuleInstance(Object moduleInstance) {
        this.moduleInstance = moduleInstance;
    }
    
    void clear() {
        methods.clear();
    }
}
