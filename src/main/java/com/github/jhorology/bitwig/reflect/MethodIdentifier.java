package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.base.Objects;
import com.github.jhorology.bitwig.rpc.RpcParamType;

class MethodIdentifier implements Comparable<MethodIdentifier> {
    private final String name;
    private final List<RpcParamType> rpcParamTypes;
    
    MethodIdentifier(String name, Type[] paramTypes) {
        this(name, Arrays.stream(paramTypes)
             .map(ReflectUtils::rpcParamTypeOf)
             .collect(Collectors.toList()));
    }
    
    MethodIdentifier(String name, List<RpcParamType> rpcParamTypes) {
        this.name = name;
        this.rpcParamTypes = rpcParamTypes;
    }

    String getName() {
        return name;
    }

    List<RpcParamType> getRpcParamTypes() {
        return rpcParamTypes;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, rpcParamTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MethodIdentifier) {
            MethodIdentifier ident = (MethodIdentifier) o;
            return name.equals(ident.name) && rpcParamTypes.equals(ident.rpcParamTypes);
        }
        return false;
    }

    @Override
    public int compareTo(MethodIdentifier other) {
        int ret = name.compareTo(other.name);
        if(ret != 0) return ret;
        int length = rpcParamTypes.size();
        ret = length - other.rpcParamTypes.size();
        if(ret != 0) return ret;
        
        if (length > 0) {
            for(int i = 0; i < length; i++) {
                ret = rpcParamTypes.get(i).compareTo(other.rpcParamTypes.get(i));
                if (ret != 0) break;
            }
        }
        return ret;
    }
}
