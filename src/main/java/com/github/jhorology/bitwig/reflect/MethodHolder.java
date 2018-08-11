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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// provided dependenices
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

// source
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * A holder class for RPC method.<br>
 * @param <T> the type of managed instance.
 */
class MethodHolder<T> extends RegistryNode<T> implements RpcMethod {
    protected final Method method;
    private final Type[] paramTypes;
    private final RpcParamType[] rpcParamTypes;
    private final boolean staticMethod;
    private final boolean varargs;
    private final boolean bitwigAPI;
    private final boolean bitwigValue;
    private final MethodIdentifier identifier;
    private String error;
    private Map<List<Object>, T> instanceCache;
    
    /**
     * 
     * @param method
     * @param nodeType
     * @param parentNode 
     * @param bankItemCount
     */
    @SuppressWarnings("unchecked")
    MethodHolder(Method method, Class<T>nodeType, RegistryNode<?> parentNode, int bankItemCount) {
        super(method.getName(), nodeType,
              method.getGenericParameterTypes(), parentNode, bankItemCount);
        this.method = method;
        this.paramTypes = parentNode instanceof MethodHolder
            ?  ArrayUtils.addAll(((MethodHolder)parentNode).paramTypes, nodeParamTypes)
            : nodeParamTypes;
        this.rpcParamTypes = Stream.of(paramTypes)
            .map(RpcParamType::of)
            .toArray(size -> new RpcParamType[size]);
        this.staticMethod = Modifier.isStatic(method.getModifiers());
        this.varargs = ReflectUtils.isVarargs(nodeParamTypes);
        this.bitwigAPI = ReflectUtils.isBitwigAPI(nodeType);
        this.bitwigValue = ReflectUtils.isBitwigValue(nodeType);
        this.identifier = new MethodIdentifier(absoluteName, rpcParamTypes);
    }

    /**
     * Returns a parameter types of this method.<br>
     * this return values maybe not same as real method's parameter types.<br>
     * <pre>
     *  below case:
     *    foobar1.foober2(a, b).foober3r(c)
     *    returns parameter types as [a,b,c]
     * </pre>
     * An implementation method of {@link RpcMethod#getParamTypes()}
     * @return
     */
    @Override
    public Type[] getParamTypes() {
        return paramTypes;
    }

    /**
     * Invoke the RPC method.<br>
     * <pre>
     *  below case:
     *    foobar1.foober2(a, b).foober3r(c)
     *    RPC calls parameters as [a,b,c]
     * </pre>
     * An implementation method of {@link RpcMethod#invoke(Object[])}
     * @params parameter(s) for invoking method.
     */
    @Override
    @SuppressWarnings({"unchecked", "UseSpecificCatch"})
    public Object invoke(Object[] params) throws Exception {
        if (params == null) {
            params = ReflectUtils.EMPTY_ARRAY;
        }
        try {
            T result;
            //  foobar1(a).foobar2(b).foobar3(c, d);
            //  rpc params:[a,b,c,d]
            //  reduce [a,b,c,d] -> [c,d]
            int start = params.length - nodeParamTypes.length;
            int end = params.length;
            Object[] thisMethodParams = ArrayUtils.subarray(params, params.length - nodeParamTypes.length, params.length);
            if (staticMethod) {
                result = (T)invokeStaticMethod(thisMethodParams);
            } else {
                start -= parentNode.nodeParamTypes.length;
                end -= nodeParamTypes.length;
                //  foobar1(a).foobar2(b).foobar3(c, d);
                //  rpc params:[a,b,c,d]
                //  reduce [a,b,c,d] -> [a,b]
                Object[] parentMethodParams = ArrayUtils.subarray(params, 0, params.length - nodeParamTypes.length);
                // cacheable parentNode instance?
                Object parentNodeInstance = parentNode.getNodeInstance(parentMethodParams);
                result = (T)invoke(parentNodeInstance, thisMethodParams);
            }
            putNodeInstance(params, result);
            return result;
        } catch (Exception ex) {
            setError(ex, "Faild invoking method.");
            throw ex;
        }
    }

    /**
     * Returns an cached managed instance.
     * @param bankIndexes
     * @return 
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T getNodeInstance(Object[] params) throws Exception{
        if (params.length != bankDimension.length) {
            return (T)invoke(params);
        }
        T nodeInstance = null;
        List<Object> key = Arrays.asList(params);
        if (instanceCache != null) {
            nodeInstance = instanceCache.get(key);
        }
        if (nodeInstance == null) {
            nodeInstance = (T)invoke(params);
        }
        return nodeInstance;
    }
    
    protected void putNodeInstance(Object[] params, T nodeInstance) {
        if (params.length != bankDimension.length) {
            return;
        }
        if (instanceCache == null) {
            instanceCache = new HashMap<>();
        }
        instanceCache.put(Arrays.asList(params), nodeInstance);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    void clear() {
    }
    
    /**
     * Returns this method is usable for RPC method or not.
     * @return
     */
    boolean isUsableForRpcMethod() {
        return ! bitwigAPI || bitwigValue;
    }

    /**
     * Return the identifier of this method.
     * @return 
     */
    MethodIdentifier getIdentifier() {
        return identifier;
    }
    
    /**
     * Retunrs an error message.
     * @return
     */
    String getError() {
        return error;
    }

    /**
     * Sets an error message.
     */
    void setError(String error) {
        this.error = error;
    }

    /**
     * Sets an error with Exception.
     * @param ex
     * @param defaultMessage
     */
    void setError(Throwable ex, String defaultMessage) {
        String errorMessage = ex.getMessage();
        if (StringUtils.isEmpty(errorMessage)) {
            errorMessage = defaultMessage;
        }
        this.error = errorMessage;
    }

    private Object invoke(Object target, Object[] params) throws IllegalAccessException,
                                                                 IllegalArgumentException,
                                                                 IllegalArgumentException,
                                                                 InvocationTargetException {
        return varargs
            ? method.invoke(target, new Object[] {params})
            : method.invoke(target, params);
    }

    private Object invokeStaticMethod(Object[] params) throws IllegalAccessException,
                                                              IllegalArgumentException,
                                                              IllegalArgumentException,
                                                              InvocationTargetException {
        return varargs
            ? method.invoke(null, new Object[] {params})
            : method.invoke(null, params);
    }
    
    /**
     * create a report object for MehodHolder.
     * @return 
     */
    Object reportRpcMethod() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("method", absoluteName);
        report.put("params", Stream.of(rpcParamTypes)
                   .map(t -> t.getExpression())
                   .collect(Collectors.toList()));
        report.put("result", rpcNodeType.getExpression());
        report.put("expression", getExpression(true));
        return report;
    }
    
    /**
     * return a RPC method expression.<be>
     * @param withReturnType
     * @retuen
     */
    String getExpression(boolean withReturnType) {
        StringBuilder sb = new StringBuilder();
        if (withReturnType) {
            sb.append(rpcNodeType.getExpression());
            sb.append(" ");
        }
        if (parentNode instanceof MethodHolder) {
            sb.append(((MethodHolder)parentNode).getExpression(false));
            sb.append(".");
            sb.append(nodeName);
        } else {
            sb.append(absoluteName);
        }
        sb.append("(");
        sb.append(Stream.of(nodeRpcParamTypes)
                  .map(t -> t.getExpression())
                  .collect(Collectors.joining(", ")));
        sb.append(")");
        return sb.toString();
    }

}
