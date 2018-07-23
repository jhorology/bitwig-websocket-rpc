package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.base.Objects;
import com.github.jhorology.bitwig.reflect.ReflectUtils.SloppyType;

class MethodIdentifier {
    private final String name;
    private final List<SloppyType> sloppyParamTypes;
    
    MethodIdentifier(String name, Type[] paramTypes) {
        this(name, Arrays.stream(paramTypes)
             .map(ReflectUtils::sloppyTypeOf)
             .collect(Collectors.toList()));
    }
    
    MethodIdentifier(String name, List<SloppyType> sloppyParamTypes) {
        this.name = name;
        this.sloppyParamTypes = sloppyParamTypes;
    }

    String getName() {
        return name;
    }

    List<ReflectUtils.SloppyType> getSloppyParamTypes() {
        return sloppyParamTypes;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, sloppyParamTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MethodIdentifier) {
            MethodIdentifier ident = (MethodIdentifier) o;
            return name.equals(ident.name) && sloppyParamTypes.equals(ident.sloppyParamTypes);
        }
        return false;
    }
}
