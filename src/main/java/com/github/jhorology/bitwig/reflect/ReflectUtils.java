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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.bitwig.extension.callback.Callback;
import com.bitwig.extension.controller.api.Subscribable;
import com.bitwig.extension.controller.api.Value;

import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * utility class
 */
public class ReflectUtils {
    /**
     * empty array of Object.
     */
    public static final Object[] EMPTY_ARRAY_OF_OBJECT = {};
    
    private static List<Method> METHODS_OF_SUBSCRIBABLE = Arrays.asList(Subscribable.class.getMethods());
    private static List<Method> METHODS_OF_VALUE = Arrays.asList(Value.class.getMethods());
    

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
                        return RpcParamType.ARRAY_OF_BOOLEAN;
                    } else {
                        return RpcParamType.ARRAY_OF_NUMBER;
                    }
                } else if (Boolean.class.isAssignableFrom(cc)) {
                    return RpcParamType.ARRAY_OF_BOOLEAN;
                } else if (Number.class.isAssignableFrom(cc)) {
                    return RpcParamType.ARRAY_OF_NUMBER;
                } else if (String.class.isAssignableFrom(cc)) {
                    return RpcParamType.ARRAY_OF_STRING;
                } else {
                    return RpcParamType.ARRAY_OF_OBJECT;
                }
            } else if (Boolean.class.isAssignableFrom(c)) {
                return RpcParamType.BOOLEAN;
            } else if (Number.class.isAssignableFrom(c)) {
                return RpcParamType.NUMBER;
            } else if (String.class.isAssignableFrom(c)) {
                return RpcParamType.STRING;
            }
        } else if (t instanceof GenericArrayType) {
            return RpcParamType.ARRAY_OF_OBJECT;
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
     * Return value of method is implemented Subscribable interface or not ?
     * @param method
     * @return
     */
    public static boolean isBitwigSubscribable(Method method) {
        return isBitwigSubscribable(method.getReturnType());
    }
    
    /**
     * Specified type is implemented Subscribable interface or not ?
     * @param type
     * @return
     */
    public static boolean isBitwigSubscribable(Class<?> type) {
        return Subscribable.class.isAssignableFrom(type);
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
     * Return value of method can be treated as RPC event or not ?
     * @param method
     * @return
     */
    public static boolean isBitwigEvent(Method method) {
        return isBitwigEvent(method.getReturnType());
    }
    
    /**
     * Specified type can be treated as RPC event or not ? <br>
     * abstract public interface Value extends Subscribable
     * @param type
     * @return
     */
    public static boolean isBitwigEvent(Class<?> type) {
        return isBitwigValue(type);
    }

    /**
     * return method is member of Subscribable interface or not.
     * @param method
     * @return
     */
    public static boolean isMemberOfSubscribable(Method method) {
        return METHODS_OF_SUBSCRIBABLE.contains(method);
    }

    /**
     * Method has any paramater of Callback or not.
     * @param method
     * @return
     */
    public static boolean hasAnyCallbackParameter(Method method) {
        return Stream.of(method.getParameterTypes())
            .anyMatch(Callback.class::isAssignableFrom);
    }

    /**
     * return method is depricated or not.
     * @param method
     * @return
     */
    public static boolean isDeprecated(Method method) {
        return method.getAnnotation(Deprecated.class) != null;
    }
}
