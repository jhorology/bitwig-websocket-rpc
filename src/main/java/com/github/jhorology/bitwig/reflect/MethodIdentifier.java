/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.reflect;

// jdk
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// provided dependencies
import com.google.common.base.Objects;

// source
import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * A identifier class that is used as hash key for MethodHoler.
 */
class MethodIdentifier implements Comparable<MethodIdentifier> {
    private final String name;
    private final List<RpcParamType> rpcParamTypes;
    
    MethodIdentifier(String name, RpcParamType[] rpcParamTypes) {
        this.name = name;
        this.rpcParamTypes = Arrays.asList(rpcParamTypes);
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
