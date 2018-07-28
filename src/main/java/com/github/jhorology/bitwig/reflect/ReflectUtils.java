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

// jvm
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// bitwig api
import com.bitwig.extension.callback.Callback;
import com.bitwig.extension.controller.api.ObjectProxy;
import com.bitwig.extension.controller.api.Subscribable;
import com.bitwig.extension.controller.api.Value;
import com.github.jhorology.bitwig.extension.Logger;

// source
import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * utility class
 */
public class ReflectUtils {
    /**
     * empty array of Object.
     */
    public static final Object[] EMPTY_ARRAY = {};
    
    private static final List<Method> BLACK_LISTED_METHODS;
    static {
        BLACK_LISTED_METHODS = new ArrayList<>();
        // ObjectProxy, not controllable from remote.
        BLACK_LISTED_METHODS.addAll(Arrays.asList(ObjectProxy.class.getMethods()));
        // ObjectProxy extends Subscribable
        // but, Subbscirbable member is OK
        BLACK_LISTED_METHODS.removeAll(Arrays.asList(Subscribable.class.getMethods()));
    }
        
    /**
     * return a sloppy type of java strict type.
     * @param t paramter type.
     * @return
     */
    public static RpcParamType rpcParamTypeOf(Type t) {
        if (t instanceof Class) {
            Class<?> c = (Class)t;
            if (c.isPrimitive()) {
                if (c.equals(boolean.class)) {
                    return RpcParamType.BOOLEAN;
                } else if (Void.TYPE.equals(c)) {
                    return RpcParamType.VOID;
                } else {
                    return RpcParamType.NUMBER;
                }
            } else if (c.isArray()) {
                Class<?> cc = c.getComponentType();
                if (cc.isPrimitive()) {
                    if (cc.equals(boolean.class)) {
                        return RpcParamType.BOOLEAN_ARRAY;
                    } else {
                        return RpcParamType.NUMBER_ARRAY;
                    }
                } else if (Boolean.class.isAssignableFrom(cc)) {
                    return RpcParamType.BOOLEAN_ARRAY;
                } else if (Number.class.isAssignableFrom(cc)) {
                    return RpcParamType.NUMBER_ARRAY;
                } else if (String.class.isAssignableFrom(cc)) {
                    return RpcParamType.STRING_ARRAY;
                } else {
                    return RpcParamType.OBJECT_ARRAY;
                }
            } else if (Boolean.class.isAssignableFrom(c)) {
                return RpcParamType.BOOLEAN;
            } else if (Number.class.isAssignableFrom(c)) {
                return RpcParamType.NUMBER;
            } else if (String.class.isAssignableFrom(c)) {
                return RpcParamType.STRING;
            }
        } else if (t instanceof GenericArrayType) {
            return RpcParamType.OBJECT_ARRAY;
        }
        return RpcParamType.OBJECT;
    };

    public static boolean isVarargs(Type[] paramTypes) {
        if (paramTypes.length == 1) {
            Type t = paramTypes[0];
            return (t instanceof Class && ((Class<?>)t).isArray())
                || t instanceof GenericArrayType;
        }
        return false;
    }
    
    /**
     * convert to varargs type if aveilable
     * [Number, Number, Number] -> [Namber[]]
     * @param rpcParamTypes
     * @return
     */
    public static RpcParamType[] toVarargs(RpcParamType[] rpcParamTypes) {
        if (rpcParamTypes.length >= 1) {
            final RpcParamType expectedType = rpcParamTypes[0];
            if(!expectedType.isArray()) {
                boolean allSameType = Stream.of(rpcParamTypes)
                    .allMatch(t -> (t == expectedType));
                if (allSameType) {
                    RpcParamType arrayType = expectedType.getArrayType();
                    return new RpcParamType[] {arrayType};
                }
            }
        }
        return null;
    }

    /**
     * return interface type is Bitwig API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigAPI(Class<?> interfaceType) {
        return isBitwigControllerAPI(interfaceType)
            || isBitwigExtensionAPI(interfaceType);
    }
        
    /**
     * return interface type is Bitwig Controller API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigControllerAPI(Class<?> interfaceType) {
        return interfaceType.getName().startsWith("com.bitwig.extension.controller.api.");
    }
    
    /**
     * return interface type is Bitwig Extension API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigExtensionAPI(Class<?> interfaceType) {
        return interfaceType.getName().startsWith("com.bitwig.extension.api.");
    }

    /**
     * Return value of method is implemented Value interface or not ?
     * it mean having addValueObserver method or not.
     * @param method
     * @return
     */
    public static boolean isBitwigValue(Method method) {
        return isBitwigValue(method.getReturnType());
    }
    
    /**
     * Return value of method is implemented Value interface or not ?
     * @param type
     * @return
     */
    public static boolean isBitwigValue(Class<?> type) {
        return Value.class.isAssignableFrom(type);
    }

    /**
     * Method has any paramater of Callback or not.
     * @param method
     * @return
     */
    public static boolean hasAnyCallbackParameter(Method method) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) return false;
        return Stream.of(types)
            .anyMatch(Callback.class::isAssignableFrom);
    }

    /**
     * Method has any paramater of OBJECT or ARRAY.
     * @param method
     * @return
     */
    public static boolean hasAnyObjectOrArrayParameter(Method method) {
        Type [] types = method.getGenericParameterTypes();
        if (types.length == 0) return false;
        return Stream.of(types)
            .map(c -> rpcParamTypeOf(c))
            .anyMatch(t -> (t == RpcParamType.OBJECT || t.isArray()));
    }

    /**
     * return method is depricated or not.
     * @param method
     * @return
     */
    public static boolean isDeprecated(Method method) {
        return method.getAnnotation(Deprecated.class) != null;
    }

    /**
     * return method is usable for RPC not.
     * @param method
     * @return
     */
    public static boolean isUsableForRpcMethod(Method method) {
        return !isDeprecated(method)
            && !hasAnyCallbackParameter(method)
            && !BLACK_LISTED_METHODS.contains(method);
    };
    
    /**
     * return method is usable for RPC event not.
     * @param method
     * @return
     */
    public static boolean isUsableForRpcEvent(Method method) {
        // is method return type implemented Value interface
        if (isBitwigValue(method)) {
            // but some 'addValueObserver' are deprecated.
            return !Stream.of(method.getReturnType().getMethods())
                .filter(m -> "addValueObserver".equals(m.getName()))
                .anyMatch(ReflectUtils::isDeprecated);
            // TODO but it's not annotated.
        }
        return false;
    };
}
