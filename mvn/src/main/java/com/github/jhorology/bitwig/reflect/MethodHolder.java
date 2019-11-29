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
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.ext.ExtApiFactory;
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
 */
class MethodHolder extends RegistryNode implements RpcMethod {
    protected final Method method;
    private final Type[] paramTypes;
    private final Type[] methodParamTypes;
    private final boolean staticMethod;
    private final boolean varargs;
    private final boolean cacheable;
    private final MethodIdentifier identifier;
    private String error;
    private Map<List<Object>, Object> resultCache;

    /**
     * Constructor.
     * @param method
     * @param nodeType
     * @param parentNode
     * @param bankItemCount
     */
    MethodHolder(Config config, Method method, Class<?>nodeType, RegistryNode parentNode, int bankItemCount) {
        super(config, method.getName(), nodeType, parentNode, bankItemCount);
        this.method = method;
        this.methodParamTypes = method.getGenericParameterTypes();
        this.paramTypes = parentNode instanceof MethodHolder
            ? ArrayUtils.addAll(((MethodHolder)parentNode).paramTypes, methodParamTypes)
            : methodParamTypes;
        this.staticMethod = Modifier.isStatic(method.getModifiers());
        this.varargs = ReflectUtils.isVarargs(method);
        // TODO this is not correct
        //  - maybe cacheable = nodeType.isAssignableFrom(ObjectProxy.class) ||
        //                      nodeType.isAssignableFrom(Bank.class) ?
        //  - need to support methods of middle of chain has arguments other than bankIndex 
        //  - Bank#setSizeOfBank(int)
        this.cacheable = (paramTypes.length == bankDimension.length)
            && (ReflectUtils.isBitwigAPI(nodeType)
                || ReflectUtils.isExtAPI(nodeType));
        this.identifier = new MethodIdentifier(absoluteName, RpcParamType.of(paramTypes));
    }

    /**
     * Returns a parameter types of this rpc method.<br>
     * this return values maybe not same as real method's parameter types.<br>
     * <pre>
     *  below case:
     *    foobar1.foober2(a, b).foober3(c)
     *  returns parameter types as [a,b,c]
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
    @SuppressWarnings({"UseSpecificCatch"})
    public Object invoke(Object[] params) throws Exception {
        if (params == null) {
            params = ReflectUtils.EMPTY_ARRAY;
        }
        Object result;
        if (cacheable && resultCache != null) {
            result = resultCache.get(Arrays.asList(params));
            if (result != null) {
                return result;
            }
        }
        try {
            //  foobar1(a).foobar2(b).foobar3(c, d);
            //  rpc params:[a,b,c,d]
            //  reduce [a,b,c,d] -> [c,d]
            int split = (parentNode instanceof MethodHolder)
                ? ((MethodHolder)parentNode).paramTypes.length : 0;
            Object[] methodParams = ArrayUtils.subarray(params, split, params.length);
            if (staticMethod) {
                result = invokeStaticMethod(methodParams);
            } else if (parentNode instanceof ModuleHolder) {
                result = invoke(((ModuleHolder)parentNode).getModuleInstance(), methodParams);
            } else if (parentNode instanceof MethodHolder) {
                //  foobar1(a).foobar2(b).foobar3(c, d);
                //  rpc params:[a,b,c,d]
                //  reduce [a,b,c,d] -> [a,b]
                Object[] parentParams = ArrayUtils.subarray(params, 0, split);
                result = invoke(((MethodHolder)parentNode).invoke(parentParams), methodParams);
            } else {
                throw new IllegalStateException();
            }
            if (cacheable) {
                if (result != null) {
                    result = ExtApiFactory.newMixinInstance(config, nodeType, result);
                }
                if (resultCache == null) {
                    resultCache = new HashMap<>();
                }
                resultCache.put(Arrays.asList(params), result);
            }
            return result;
        } catch (Exception ex) {
            StringBuilder msg = new StringBuilder("Faild invoking method.");
            Exception err = ex;
            if (ex instanceof InvocationTargetException) {
                Throwable cause = ex.getCause();
                if (cause != null && cause instanceof Exception) {
                    err = (Exception)cause;
                    msg.append(" caused by:");
                    msg.append(cause.getMessage());
                }
            }
            setError(err,  msg.toString());
            throw err;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void clear() {
        if (cacheable && resultCache != null) {
            resultCache.clear();
        }
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

    /**
     * create a report object for MehodHolder.
     * @return
     */
    Object reportRpcMethod() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("method", absoluteName);
        // TODO if nodeType or paramType are implemented Itreator, result type should be array "[]".
        // it's difficult to resolve generic type of Iterator.
        report.put("params", Stream.of(paramTypes)
                   .map(t -> RpcParamType.of(t))
                   .map(t -> t.getExpression())
                   .collect(Collectors.toList()));
        report.put("result", rpcNodeType.getExpression());
        report.put("expression", getExpression(true));
        return report;
    }

    /**
     * return a Java API expression of this node.<be>
     * @param returnType
     * @retuen
     */
    String getExpression(boolean returnType) {
        StringBuilder sb = new StringBuilder();
        if (returnType) {
            sb.append(nodeType.getSimpleName());
            sb.append(" ");
        }
        if (parentNode instanceof MethodHolder) {
            sb.append(((MethodHolder)parentNode).getExpression(false));
            sb.append(".");
        } else {
            sb.append(parentNode.nodeType.getSimpleName());
            sb.append("#");
        }
        sb.append(method.getName());
        sb.append("(");
        sb.append(Stream.of(method.getGenericParameterTypes())
            .map(t -> t.getTypeName())
            .collect(Collectors.joining(", ")));
        sb.append(")");
        return sb.toString();
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
     * Returns a java expression of this method.
     * @return 
     */
    String getJavaExpression() {
        return getJavaExpression(true, true);
    }
    
    String getJavaExpression(boolean returnType, boolean declaringClass) {
        return ReflectUtils.javaExpression(this.method, returnType, declaringClass);
    }
    
}
