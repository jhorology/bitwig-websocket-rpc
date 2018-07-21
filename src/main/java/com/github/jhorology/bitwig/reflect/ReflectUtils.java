package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class ReflectUtils {
    public static enum SloppyType {
        BOOLEAN,
        NUMBER,
        STRING,
        OBJECT,
        ARRAY_OF_BOOLEAN,
        ARRAY_OF_NUMBER,
        ARRAY_OF_STRING,
        ARRAY_OF_OBJECT;

        public SloppyType arrayTypeOf() {
            return isArray()
                ? SloppyType.valueOf(this.name().substring(9))
                : null;
        }
        
        public SloppyType toArrayType() {
            return isArray()
                ? null
                : SloppyType.valueOf("ARRAY_OF_" + this.name());
        }
        
        public boolean isArray() {
            return this.name().startsWith("ARRAY_OF_");
        }
    };

    public static SloppyType toSloppyType(Type t) {
        if (t instanceof Class) {
            Class<?> c = (Class)t;
            if (c.isPrimitive()) {
                if (c.equals(boolean.class)) {
                    return SloppyType.BOOLEAN;
                } else {
                    return SloppyType.NUMBER;
                }
            } else if (c.isArray()) {
                Class<?> cc = c.getComponentType();
                if (cc.isPrimitive()) {
                    if (cc.equals(boolean.class)) {
                        return SloppyType.ARRAY_OF_BOOLEAN;
                    } else {
                        return SloppyType.ARRAY_OF_NUMBER;
                    }
                } else if (Boolean.class.isAssignableFrom(cc)) {
                    return SloppyType.ARRAY_OF_BOOLEAN;
                } else if (Number.class.isAssignableFrom(cc)) {
                    return SloppyType.ARRAY_OF_NUMBER;
                } else if (String.class.isAssignableFrom(cc)) {
                    return SloppyType.ARRAY_OF_STRING;
                } else {
                    return SloppyType.ARRAY_OF_OBJECT;
                }
            } else if (Boolean.class.isAssignableFrom(c)) {
                return SloppyType.BOOLEAN;
            } else if (Number.class.isAssignableFrom(c)) {
                return SloppyType.NUMBER;
            } else if (String.class.isAssignableFrom(c)) {
                return SloppyType.STRING;
            }
        } else if (t instanceof GenericArrayType) {
            return SloppyType.ARRAY_OF_OBJECT;
        }
        return SloppyType.OBJECT;
    };
}
