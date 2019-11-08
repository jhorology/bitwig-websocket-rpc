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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * RPC paramater type enum that is used for loose matching parameters.
 */
public enum RpcParamType {
    /**
     * void type.
     */
    VOID("void", false, null),
    
    /**
     * bool type.
     */
    BOOLEAN("boolean", false, null),
    
    /**
     * number type.
     */
    NUMBER("Number", false, null),
    
    /**
     * string type.
     */
    STRING("String", false, null),
    
    /**
     * object type.
     */
    OBJECT("Object", false, null),
    
    /**
     * array of BOOLEANs type.
     */
    BOOLEAN_ARRAY("boolean[]", true, BOOLEAN),
    
    /**
     * array of NUMBERs type.
     */
    NUMBER_ARRAY("Number[]", true, NUMBER),
    
    /**
     *  array of STRINGs type.
     */
    STRING_ARRAY("String[]", true, STRING),
    
    /**
     * array of OBJECTs type.
     */
    OBJECT_ARRAY("Object[]", true, OBJECT);

    private final String expression;
    private final boolean array;
    private final RpcParamType componentType;
    private RpcParamType arrayType;
    
    private RpcParamType(String expression, boolean array, RpcParamType componentType) {
        this.expression = expression;
        this.array = array;
        this.componentType = componentType;
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
    public RpcParamType getComponentType() {
        return componentType;
    }

    /**
     * return a array type of this enum value if this enum is component type, 
     * @return
     */
    public RpcParamType getArrayType() {
        if (array || this == VOID) {
            return null;
        }
        if (arrayType != null) {
            return arrayType;
        }
        arrayType = RpcParamType.valueOf(this.name() + "_ARRAY");
        return arrayType;
    }

    /**
     * return this type is array or not.
     * @return
     */
    public boolean isArray() {
        return array;
    }
    
    /**
     * Return a RpcParamType type that represents the specified type.
     * @param type strickt java type
     * @return the enum value of RpcParamType
     */
    public static RpcParamType of(Type type) {
        if (type instanceof Class) {
            Class<?> c = (Class<?>)type;
            if (c.isPrimitive()) {
                if (c.equals(boolean.class)) {
                    return RpcParamType.BOOLEAN;
                } else if (Void.TYPE.equals(c)) {
                    return RpcParamType.VOID;
                } else {
                    return RpcParamType.NUMBER;
                }
            } else if (c.isEnum()) {
                return RpcParamType.STRING;
            } else if (c.isArray()) {
                Class<?> cc = c.getComponentType();
                if (cc.isPrimitive()) {
                    if (cc.equals(boolean.class)) {
                        return RpcParamType.BOOLEAN_ARRAY;
                    } else {
                        return RpcParamType.NUMBER_ARRAY;
                    }
                } else if (c.isEnum()) {
                    return RpcParamType.STRING_ARRAY;
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
        } else if (type instanceof GenericArrayType) {
            return RpcParamType.OBJECT_ARRAY;
        }
        return RpcParamType.OBJECT;
    }
};
