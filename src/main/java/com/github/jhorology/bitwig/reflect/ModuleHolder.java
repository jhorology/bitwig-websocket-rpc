package com.github.jhorology.bitwig.reflect;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class ModuleHolder {
    private final Multimap<String, MethodHolder> methods;
    private Object moduleInstance;
    
    public ModuleHolder(String moduleName, Class<?> interfaceType) throws IllegalAccessException {
        methods = ArrayListMultimap.create();
        for(Method method : interfaceType.getMethods()) {
            if(Modifier.isPublic(method.getModifiers())) {
                methods.put(method.getName(), new MethodHolder(this, method));
            }
        }
    }
    
    public ModuleHolder(String moduleName, Class<?> interfaceType, Object moduleInstance) throws IllegalAccessException {
        this(moduleName, interfaceType);
        this.moduleInstance = moduleInstance;
    }

    public MethodHolder getMethodHolder(String name, int paramCount) {
        Collection<MethodHolder> sameNamedMethods = methods.get(name);
        if (sameNamedMethods == null || sameNamedMethods.isEmpty()) {
            return null;
        }
        for(MethodHolder method : sameNamedMethods) {
            if (method.getParamCount() == paramCount) {
                return method;
            }
        }
        return null;
    }
    
    public Object getModuleInstance() {
        return moduleInstance;
    }
    
    public void setModuleInstance(Object moduleInstance) {
        this.moduleInstance = moduleInstance;
    }
    
    public void clear() {
        methods.clear();
    }
}
