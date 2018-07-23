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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import com.github.jhorology.bitwig.rpc.RpcMethod;

public class MethodHolder implements RpcMethod {
    private final ModuleHolder<?> module;
    private final Method method;
    protected final MethodHolder parentChain;
    
    private final MethodIdentifier identifier;
    private final Type[] paramTypes;
    private final boolean staticMethod;
    private final boolean subscribable;
    private final boolean varargs;
    
    protected boolean havingChildChain;
    // cache the return value of method;
    protected Object returnValue;

    MethodHolder(ModuleHolder<?> module, Method method) {
        this(module, method, null);
    }
    
    MethodHolder(ModuleHolder<?> module, Method method, MethodHolder parantChain) {
        this.module = module;
        this.method = method;
        this.parentChain = parantChain;
        if (parantChain != null) {
            parantChain.setHavingChildChain(true);
        }
        this.paramTypes = method.getGenericParameterTypes();
        this.identifier = new MethodIdentifier(getName(), paramTypes);
        this.staticMethod = Modifier.isStatic(method.getModifiers());
        this.subscribable = ReflectUtils.isBitwigSubscribable(method);
        this.varargs = ReflectUtils.isVarargs(paramTypes);
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
        return method.getName();
    }

    /**
     * return a registry key name without module name.<be>
     * @retuen
     */
    String getName() {
        StringBuilder sb = new StringBuilder(getSimpleName());
        if (getParentChain() != null) {
            sb.insert(0, ".");
            sb.insert(0, getParentChain().getName());
        }
        return sb.toString();
    }
    
    /**
     * return a absolute registry key name.<be>
     * @retuen
     */
    String getAbsoluteName() {
        StringBuilder sb = new StringBuilder(getSimpleName());
        sb.insert(0, ".");
        if (getParentChain() != null) {
            sb.insert(0, getParentChain().getAbsoluteName());
        } else {
            sb.insert(0, module.getModuleName());
        }
        return sb.toString();
    }

    protected void setHavingChildChain(boolean havingChildChain) {
        this.havingChildChain = havingChildChain;
    }

    protected Object getReturnValue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO
        // is caching OK ?
        // support parentChain having parameters.
        if (returnValue == null) {
            returnValue = invoke(ReflectUtils.EMPTY_ARRAY_OF_OBJECT);
        }
        return returnValue;
    }
    
    protected Object getMethodInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (parentChain != null) {
            return parentChain.getReturnValue();
        }
        return module.getModuleInstance();
    }
    
    @Override
    public Type[] getParamTypes() {
        return paramTypes;
    }

    @Override
    public Object invoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        returnValue = internalInvoke(params);
        return returnValue;
    }

    protected Object internalInvoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, IllegalArgumentException, InvocationTargetException {
        if (params == null || params.length == 0) {
            return method.invoke(staticMethod ? null : getMethodInstance());
        } else {
            return varargs
                ? method.invoke(staticMethod ? null : getMethodInstance(), new Object[] {params})
                : method.invoke(staticMethod ? null : getMethodInstance(), params);
        }
    }
}
