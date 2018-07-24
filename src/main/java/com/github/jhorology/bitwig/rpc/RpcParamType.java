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
package com.github.jhorology.bitwig.rpc;

/**
 * RPC paramater type enum that is uses for lazy matching parameters.
 */
public enum RpcParamType {
    /**
     * identify void type.
     */
    VOID("void"),
    
    /**
     * identify bool.
     */
    BOOLEAN("boolean"),
    
    /**
     * identify number.
     */
    NUMBER("Number"),
    
    /**
     * identify string.
     */
    STRING("String"),
    
    /**
     * identify object.
     */
    OBJECT("Object"),
    
    /**
     * identify array of bool values.
     */
    ARRAY_OF_BOOLEAN("boolean[]"),
    
    /**
     * identify array of numbers.
     */
    ARRAY_OF_NUMBER("Number[]"),
    
    /**
     * identify array of strings.
     */
    ARRAY_OF_STRING("String[]"),
    
    /**
     * identify array of objects.
     */
    ARRAY_OF_OBJECT("Object[]");

    private String expression;

    private RpcParamType(String expression) {
        this.expression = expression;
    }

    /**
     * return a expression of thie enamu value.
     * @return
     */
    public String getExpression() {
        return expression;
    }

    /**
     * return a component type of this enum value if this enum is array type, 
     * @return
     */
    public RpcParamType arrayTypeOf() {
        return isArray()
            ? RpcParamType.valueOf(this.name().substring(9))
            : null;
    }

    /**
     * return a array type of this enum value if this enum is component type, 
     * @return
     */
    public RpcParamType toArrayType() {
        return this != VOID && !isArray()
            ? RpcParamType.valueOf("ARRAY_OF_" + this.name())
            : null;
    }

    /**
     * return this type is array or not.
     * @return
     */
    public boolean isArray() {
        return this.name().startsWith("ARRAY_OF_");
    }
};
