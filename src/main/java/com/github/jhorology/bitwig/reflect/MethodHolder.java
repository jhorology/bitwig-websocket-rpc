package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;


public class MethodHolder {
    private final ModuleHolder module;
    private final MethodIdentifier identifier;
    private final Method method;
    private final Type[] paramTypes;
    private final boolean isStatic;
    
    MethodHolder(ModuleHolder module, Method method) {
        this.module = module;
        this.paramTypes = method.getGenericParameterTypes();
        this.identifier = new MethodIdentifier(method.getName(), paramTypes);
        this.method = method;
        this.isStatic = Modifier.isStatic(method.getModifiers());
    }

    public MethodIdentifier getIdentifier() {
        return identifier;
    }
    
    public boolean isVarargs() {
        return identifier.isVarargs();
    }

    public Type[] getParamTypes() {
        return paramTypes;
    }

    public List<SloppyType> getSloppyParamTypes() {
        return identifier.getSloppyParamTypes();
    }
    
    public Object invokeWithVarags(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        return method.invoke(isStatic ? null : module.getModuleInstance(), new Object[] {params});
    }

    public Object invoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        if (params == null || params.length == 0) {
            return method.invoke(isStatic ? null : module.getModuleInstance());
        } else {
            return method.invoke(isStatic ? null : module.getModuleInstance(), params);
        }
    }
}
