package com.github.jhorology.bitwig.websocket;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Objects;

public class RpcMethodIdentifier {
    private final String name;
    private final List<Class<?>> parameterTypes;

    public RpcMethodIdentifier(Method method) {
        this.name = method.getName();
        this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, parameterTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RpcMethodIdentifier) {
            RpcMethodIdentifier ident = (RpcMethodIdentifier) o;
            return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
        }
        return false;
    }
}
