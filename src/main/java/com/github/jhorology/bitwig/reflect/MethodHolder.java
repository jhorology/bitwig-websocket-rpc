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
import java.util.LinkedHashMap;
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
 * A holder class for invocable method.<br>
 */
public class MethodHolder implements RpcMethod {
    protected final ModuleHolder<?> owner;
    
    private final Method method;
    private final MethodHolder parentChain;
    
    private final MethodIdentifier identifier;
    private final Type[] internalParamTypes;
    private final Type[] paramTypes;
    private final RpcParamType[] internalRpcParamTypes;
    private final RpcParamType[] rpcParamTypes;
    private final Class<?> returnType;
    private final RpcParamType rpcReturnType;
    private final boolean staticMethod;
    private final boolean varargs;
    protected final String simpleName;
    protected final String name;
    protected final String absoluteName;
    
    protected boolean havingChildChain;
    // cache the return value of method;
    protected Object returnValue;
    private String error;

    MethodHolder(ModuleHolder<?> owner, Method method) {
        this(owner, method, null);
    }
    
    MethodHolder(ModuleHolder<?> owner, Method method, MethodHolder parantChain) {
        this.owner = owner;
        this.method = method;
        this.parentChain = parantChain;
        this.internalParamTypes = method.getGenericParameterTypes();
        this.internalRpcParamTypes = Stream.of(internalParamTypes)
            .map(RpcParamType::of)
            .toArray(size -> new RpcParamType[size]);
        
        // add parentChain method's parameters getParamTypes()
        this.staticMethod = Modifier.isStatic(method.getModifiers());
        this.varargs = ReflectUtils.isVarargs(internalParamTypes);
        this.simpleName = method.getName();
        this.returnType = method.getReturnType();
        this.rpcReturnType = RpcParamType.of(this.returnType);
        if (parantChain == null) {
            this.name = simpleName;
            this.absoluteName = owner.getModuleName() + "." + simpleName;
            this.paramTypes = this.internalParamTypes;
            this.rpcParamTypes = this.internalRpcParamTypes;
        } else {
            this.name = parantChain.getName() + "." + this.simpleName;
            this.absoluteName = parantChain.getAbsoluteName() + "." + this.simpleName;
            this.paramTypes =  ArrayUtils.addAll(parentChain.getParamTypes(), internalParamTypes);
            this.rpcParamTypes =  ArrayUtils.addAll(parentChain.getRpcParamTypes(), internalRpcParamTypes);
            parantChain.setHavingChildChain(true);
        }
        this.identifier = new MethodIdentifier(getName(), rpcParamTypes);
    }

    /**
     * retuan a parameter types of this method.<br>
     * this return values maybe not same as real method's parameter types.
     *  below case:
     *     foobar.getFoobar(a, b).doFoobar(c)
     *  return parameter types as [a,b,c]
     * @return
     */
    @Override
    public Type[] getParamTypes() {
        return paramTypes;
    }

    /**
     * Invoke the RPC method.
     * @params parameter(s) for invoking method.
     */
    @Override
    public Object invoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        if (params == null) {
            params = ReflectUtils.EMPTY_ARRAY;
        }
        if (parentChain != null) {
            // RPC call with parameter [a,b,c].
            // need to reduce real method's parameter [c]
            //   application.foobar(a, b).foobar(c)
            Object[] thisMethodParams = ArrayUtils.subarray(params, params.length - internalParamTypes.length, params.length);
            returnValue = internalInvoke(thisMethodParams);
            return returnValue;
        }
        returnValue = internalInvoke(params);
        return returnValue;
    }

    MethodIdentifier getIdentifier() {
        return identifier;
    }

    MethodHolder getParentChain() {
        return parentChain;
    }
    
    /**
     * return a simple method name.<be>
     * It's equivalent to Method#getName()
     * @retuen
     */
    String getSimpleName() {
        return simpleName;
    }

    /**
     * return a registry key name without module name.<be>
     * @retuen
     */
    String getName() {
        return name;
    }
    
    /**
     * return a absolute registry key name.<be>
     * @retuen
     */
    String getAbsoluteName() {
        return absoluteName;
    }
    
    /**
     * return a parameter types of this method.<be>
     * @retuen
     */
    RpcParamType[] getRpcParamTypes() {
        return rpcParamTypes;
    }

    /**
     * return a return type of this method.<be>
     * @retuen
     */
    RpcParamType getRpcReturnType() {
        return rpcReturnType;
    }
    
    /**
     * return a RPC method expression.<be>
     * @param withReturnType
     * @retuen
     */
    String getExpression(boolean withReturnType) {
        StringBuilder sb = new StringBuilder();
        if (withReturnType) {
            sb.append(rpcReturnType.getExpression());
            sb.append(" ");
        }
        if (parentChain != null) {
            sb.append(parentChain.getExpression(false));
            sb.append(".");
            sb.append(simpleName);
        } else {
            sb.append(absoluteName);
        }
        sb.append("(");
        sb.append(Stream.of(internalRpcParamTypes)
                  .map(t -> t.getExpression())
                  .collect(Collectors.joining(", ")));
        sb.append(")");
        return sb.toString();
    }

    String getError() {
        return error;
    }
    
    void setError(String error) {
        this.error = error;
    }
    
    void setError(Throwable ex, String defaultMessage) {
        String errorMessage = ex.getMessage();
        if (StringUtils.isEmpty(errorMessage)) {
            errorMessage = defaultMessage;
        }
        this.error = errorMessage;
    }
    
    protected void setHavingChildChain(boolean havingChildChain) {
        this.havingChildChain = havingChildChain;
    }
    
    protected boolean hasChildChain() {
        return havingChildChain;
    }

    protected Object getReturnValue(Object[] params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO
        // need do something with ObjectProxy
        if (returnValue == null) {
            returnValue = invoke(params);
        }
        return returnValue;
    }
    
    protected Object getModuleInstance(Object[] params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (parentChain != null) {
            // RPC call with parameter [a,b,c].
            // need to reduce real parent method's parameter [a, b]
            //   application.foobar(a, b).foobar(c)
            Object[] parentMethodParams = ArrayUtils.subarray(params, 0, params.length - internalParamTypes.length);
            return parentChain.getReturnValue(parentMethodParams);
        }
        return owner.getModuleInstance();
    }

    protected Object internalInvoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        Object ret;
        if (staticMethod) {
            if (varargs) {
                ret = method.invoke(null, new Object[] {params});
            } else {
                ret = method.invoke(null, params);
            }
        } else {
            Object target = getModuleInstance(params);
            if (varargs) {
                ret = method.invoke(target, new Object[] {params});
            } else {
                ret = method.invoke(target, params);
            }
        }
        return ret;
    }

    /**
     * cleanup this instance.
     */
    void clear() {
    }

    /**
     * create a report object for MehodHolder.
     * @param eh
     * @return 
     */
    Object reportRpcMethod() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("method", absoluteName);
        report.put("params", Stream.of(rpcParamTypes)
                   .map(t -> t.getExpression())
                   .collect(Collectors.toList()));
        report.put("result", rpcReturnType.getExpression());
        report.put("expression", getExpression(true));
        return report;
    }
}
