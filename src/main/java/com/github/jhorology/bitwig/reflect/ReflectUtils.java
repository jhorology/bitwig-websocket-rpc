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
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.Subscribable;
import com.bitwig.extension.controller.api.Value;

// source
import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * utility class
 */
@SuppressWarnings("UseSpecificCatch")
public class ReflectUtils {
    /**
     * empty array of Object.
     */
    public static final Object[] EMPTY_ARRAY = {};
    public static final Class<?>[] EMPTY_CLASS_ARRAY = {};
    private static final List<Method> BLACK_LISTED_METHODS;
    private static final List<Method> BLACK_LISTED_EVENTS;
    static {
        BLACK_LISTED_METHODS = new ArrayList<>();
        BLACK_LISTED_EVENTS = new ArrayList<>();
        try {
            // public interface Value<? extends ValueChangedCallback> extneds Subscribable
            // public interface ObjectProxy extends Subscribable
            
            // ObjectProxy, not controllable from remote.
            BLACK_LISTED_METHODS.addAll(Arrays.asList(ObjectProxy.class.getMethods()));
            // ObjectProxy extends Subscribable
            // but, Subbscirbable member is OK
            BLACK_LISTED_METHODS.removeAll(Arrays.asList(Subscribable.class.getMethods()));
            // markInterested is probably same as Subscribable#subscibe()
            BLACK_LISTED_METHODS.add(Value.class.getMethod("markInterested", EMPTY_CLASS_ARRAY));

        } catch (Exception ex) {
        }
    }

    /**
     * return method is usable for RPC not.
     * @param method
     * @return
     */
    public static boolean isUsableForRpcMethod(Method method) {
        return !isDeprecated(method)
            && !isModuleFactory(method)
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
        // except Parameter interface:
        //
        // This has been deprecated since API version 2: Use value().addValueObserver(callback) instead
        //
        if (isBitwigValue(method) && !isBitwigParameter(method)) {
            // but some 'addValueObserver' are deprecated.
            return !Stream.of(method.getReturnType().getMethods())
                .filter(m -> "addValueObserver".equals(m.getName()))
                .anyMatch(ReflectUtils::isDeprecated);
        }
        return false;
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
     * convert to varargs type if available
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
     * Specified interfaceType is Bitwig Extension API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigExtensionAPI(Class<?> interfaceType) {
        return interfaceType.getName().startsWith("com.bitwig.extension.api.");
    }

    /**
     * Return value of spcified method is implemented Value interface or not ?
     * it mean having addValueObserver method or not.
     * @param method
     * @return
     */
    public static boolean isBitwigValue(Method method) {
        return isBitwigValue(method.getReturnType());
    }
    
    /**
     * Specifid interfaceType is implemented Value interface or not ?
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigValue(Class<?> interfaceType) {
        return Value.class.isAssignableFrom(interfaceType);
    }

    /**
     * Return value of specified method is implemented Prameter interface or not ?
     * @param method
     * @return
     */
    public static boolean isBitwigParameter(Method method) {
        return isBitwigParameter(method.getReturnType());
    }
    
    /**
     * Secified interfaceType is implemented Prameter interface or not ?
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigParameter(Class<?> interfaceType) {
        return Parameter.class.isAssignableFrom(interfaceType);
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
            .map(RpcParamType::of)
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
     * return class is depricated or not.
     * @param clazz
     * @return
     */
    public static boolean isDeprecated(Class<?> clazz) {
        return clazz.getAnnotation(Deprecated.class) != null;
    }
    
    /**
     * Retun specified method is core module factory or not.
     *  it's should be managed as RPC module.
     * @param method
     * @return
     */
    public static boolean isModuleFactory(Method method) {
        // TODO
        // need to investigate core modules that can be instantiated at only within init.
        //
        // this is enough for now.
        return method.getName().startsWith("create")
            && !isBitwigValue(method.getReturnType())
            && isBitwigAPI(method.getReturnType());
    }

}
