package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Objects;

public class MethodIdentifier {
    private final String name;
    private final List<Class<?>> parameterTypes;

    public MethodIdentifier(Method method) {
        this.name = method.getName();
        this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, parameterTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MethodIdentifier) {
            MethodIdentifier ident = (MethodIdentifier) o;
            return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
        }
        return false;
    }
}
