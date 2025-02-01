package org.dflib.collection;

import java.lang.reflect.Array;

/**
 * @since 2.0.0
 */
// Not calling this "Arrays" to avoid confusion with "java.util.Arrays"
public class JavaArrays {

    public static <T> T[] newArray(Class<?> componentType, int len) {
        // TODO: convert primitive types to their corresponding boxed types
        return Object.class == componentType || componentType.isPrimitive()
                ? (T[]) new Object[len]
                : (T[]) Array.newInstance(componentType, len);
    }
}
