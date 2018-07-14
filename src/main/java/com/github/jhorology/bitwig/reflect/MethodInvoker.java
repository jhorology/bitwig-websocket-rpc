package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodInvoker {
    private Object module;
    private Method method;
    
    public MethodInvoker(Object module, Method method) {
        this.module = module;
        this.method = method;
    }
    
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }
    
    public int getParameterCount() {
        return method.getParameterCount();
    }
    
    public Object invoke() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        return method.invoke(isStatic ? null : module);
    }

    public Object invoke(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (getParameterCount() == 0) {
            return method.invoke(isStatic ? null : module);
        } else {
            return method.invoke(isStatic ? null : module, args);
        }
    }
}
