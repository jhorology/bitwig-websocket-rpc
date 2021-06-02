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
import java.util.stream.Stream;

/**
 * RPC parameter type enum that is used for loose matching java parameters.
 */
public class RpcParamType implements Comparable<RpcParamType> {

  /**
   * void type.
   */
  public static final RpcParamType VOID = newType(0x0000, "void");

  /**
   * bool type.
   */
  public static final RpcParamType BOOLEAN = newType(0x0001, "boolean");

  /**
   * number type.
   */
  public static final RpcParamType NUMBER = newType(0x0002, "number");

  /**
   * string type.
   */
  public static final RpcParamType STRING = newType(0x0003, "string");

  /**
   * object type.
   */
  public static final RpcParamType OBJECT = newType(0x0004, "object");

  /**
   * any type.
   */
  public static final RpcParamType ANY = newType(0x0005, "any");

  /**
   * array of BOOLEANs type.
   */
  public static final RpcParamType BOOLEAN_ARRAY = newArrayType(
    0x0010,
    "boolean[]",
    BOOLEAN
  );

  /**
   * array of NUMBERs type.
   */
  public static final RpcParamType NUMBER_ARRAY = newArrayType(
    0x0011,
    "number[]",
    NUMBER
  );

  /**
   *  array of STRINGs type.
   */
  public static final RpcParamType STRING_ARRAY = newArrayType(
    0x0012,
    "string[]",
    STRING
  );

  /**
   * array of OBJECTs type.
   */
  public static final RpcParamType OBJECT_ARRAY = newArrayType(
    0x0013,
    "object[]",
    OBJECT
  );

  /**
   * array of ANYs type.
   */
  public static final RpcParamType ANY_ARRAY = newArrayType(
    0x0014,
    "any[]",
    ANY
  );

  private final int hashCode;
  private final String expression;
  private final RpcParamType componentType;

  private RpcParamType(
    int hashCode,
    String expression,
    RpcParamType componentType
  ) {
    this.hashCode = hashCode;
    this.expression = expression;
    this.componentType = componentType;
  }

  private static RpcParamType newType(int hashCode, String expression) {
    return new RpcParamType(hashCode, expression, null);
  }

  private static RpcParamType newArrayType(
    int hashCode,
    String expression,
    RpcParamType componentType
  ) {
    return new RpcParamType(hashCode, expression, componentType);
  }

  @Override
  public int hashCode() {
    // 0 to  matching ANY,ANY_ARRAY
    //
    // jdk HashMap
    //
    // if (e.hash == hash &&
    //         ((k = e.key) == key || (key != null && key.equals(k))))
    //
    //return hashCode;
    return 0;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof RpcParamType)) {
      return false;
    }
    RpcParamType o = (RpcParamType) other;
    return (
      this == o ||
      (
        this.isArray() == o.isArray() &&
        (this == ANY || o == ANY || this == ANY_ARRAY || o == ANY_ARRAY)
      )
    );
  }

  @Override
  public String toString() {
    return expression;
  }

  @Override
  public int compareTo(RpcParamType other) {
    return ((Integer) hashCode).compareTo(other.hashCode);
  }

  /**
   * return a expression of the enamu value.
   * @return
   */
  public String getExpression() {
    return expression;
  }

  /**
   * return a component type of this enum value if this enum is array type,
   * @return
   */
  public RpcParamType getArrayType() {
    RpcParamType[] types = {
      null,
      BOOLEAN_ARRAY,
      NUMBER_ARRAY,
      STRING_ARRAY,
      OBJECT_ARRAY,
      ANY_ARRAY,
    };
    return hashCode < 6 ? types[hashCode] : null;
  }

  /**
   * return a component type of this enum value if this enum is array type,
   * @return
   */
  public RpcParamType getComponentType() {
    return componentType;
  }

  /**
   * return this type is array or not.
   * @return
   */
  public boolean isArray() {
    return componentType != null;
  }

  /**
   * Return a array of RpcParamType types that represents the specified array of types.
   * @param types array of strict java type
   * @return the aarray of enum value of RpcParamType
   */
  public static RpcParamType[] of(Type[] types) {
    return Stream
      .of(types)
      .map(t -> of(t, false))
      .toArray(size -> new RpcParamType[size]);
  }

  /**
   * Return a array of RpcParamType types that represents the specified array of types.
   * @param types array of strict java type
   * @param allowAny
   * @return the aarray of enum value of RpcParamType
   */
  public static RpcParamType[] of(Type[] types, boolean allowAny) {
    return Stream
      .of(types)
      .map(t -> of(t, allowAny))
      .toArray(size -> new RpcParamType[size]);
  }

  /**
   * Return a RpcParamType type that represents the specified type.
   * @param type strict java type
   * @return the enum value of RpcParamType
   */
  public static RpcParamType of(Type type) {
    return of(type, false);
  }

  /**
   * Return a RpcParamType type that represents the specified type.
   * @param type strict java type
   * @param allowAny
   * @return the enum value of RpcParamType
   */
  public static RpcParamType of(Type type, boolean allowAny) {
    if (type instanceof Class) {
      Class<?> c = (Class<?>) type;
      if (c.isArray()) {
        return of(c.getComponentType(), allowAny).getArrayType();
      } else if (c.isPrimitive()) {
        if (c == boolean.class) {
          return BOOLEAN;
        } else if (Void.TYPE == c) {
          return VOID;
        } else {
          return NUMBER;
        }
      } else if (allowAny && Object.class == c) {
        return ANY;
      } else if (c.isEnum()) {
        return STRING;
      } else if (Boolean.class.isAssignableFrom(c)) {
        return BOOLEAN;
      } else if (Number.class.isAssignableFrom(c)) {
        return NUMBER;
      } else if (String.class.isAssignableFrom(c)) {
        return STRING;
      }
    } else if (type instanceof GenericArrayType) {
      return OBJECT_ARRAY;
    }
    return OBJECT;
  }
}
