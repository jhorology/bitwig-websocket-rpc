package com.github.jhorology.bitwig.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * utility class
 */
public class ReflectUtils {
    /**
     * sloppy type enum that uses for lazy matching parameters.
     */
    public static enum SloppyType {
        BOOLEAN,
        NUMBER,
        STRING,
        OBJECT,
        ARRAY_OF_BOOLEAN,
        ARRAY_OF_NUMBER,
        ARRAY_OF_STRING,
        ARRAY_OF_OBJECT;

        /**
         * return a component type of this enum value if this enum is array type, 
         * @return
         */
        public SloppyType arrayTypeOf() {
            return isArray()
                ? SloppyType.valueOf(this.name().substring(9))
                : null;
        }
        
        /**
         * return a array type of this enum value if this enum is component type, 
         * @return
         */
        public SloppyType toArrayType() {
            return isArray()
                ? null
                : SloppyType.valueOf("ARRAY_OF_" + this.name());
        }
        
        /**
         * return this type is array or not.
         * @return
         */
        public boolean isArray() {
            return this.name().startsWith("ARRAY_OF_");
        }
    };

    /**
     * return a sloppy type of java strict type.
     * @param t paramter type.
     * @return
     */
    public static SloppyType sloppyTypeOf(Type t) {
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
}
