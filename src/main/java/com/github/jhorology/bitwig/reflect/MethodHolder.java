package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;


public class MethodHolder {
    private final ModuleHolder moduleHolder;
    private final Method method;
    private final boolean isStatic;
    private final Type[] paramTypes;
    
    public MethodHolder(ModuleHolder moduleHolder, Method method) throws IllegalAccessException {
        this.moduleHolder = moduleHolder;
        this.method = method;
        this.isStatic = Modifier.isStatic(method.getModifiers());
        this.paramTypes = method.getGenericParameterTypes();
    }

    public Type[] getParamTypes() {
        return paramTypes;
    }
    
    public int getParamCount() {
        return paramTypes == null ? 0 : paramTypes.length;
    }

    public Object invoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        int size = getParamCount();
        if (size == 0) {
            return method.invoke(isStatic ? null : moduleHolder.getModuleInstance());
        } else {
            return method.invoke(isStatic ? null : moduleHolder.getModuleInstance(), params);
        }
    }
}
