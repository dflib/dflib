package org.dflib;

import org.dflib.map.AnyToPrimitiveMappers;
import org.dflib.map.StringToPrimitiveMappers;

/**
 * A mapper of some value to a boolean. Used in various data conversion APIs.
 */
public interface BoolValueMapper<V> {

    /**
     * @since 2.0.0
     */
    static BoolValueMapper<Object> of() {
        return AnyToPrimitiveMappers::toBool;
    }

    /**
     * @since 2.0.0
     */
    static BoolValueMapper<String> ofStr() {
        return StringToPrimitiveMappers::toBool;
    }

    /**
     * @deprecated in favor of {@link #of()} that has slightly different behavior, treating non-zero numbers as "true"
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static BoolValueMapper<Object> fromObject() {
        return o -> {

            if (o instanceof Boolean) {
                return ((Boolean) o).booleanValue();
            }

            String s = o != null ? o.toString() : null;

            // null-safe... "parseBoolean" returns false for null
            return Boolean.parseBoolean(s);
        };
    }

    /**
     * @deprecated in favor of {@link #ofStr()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static BoolValueMapper<String> fromString() {
        return ofStr();
    }

    boolean map(V v);
}
