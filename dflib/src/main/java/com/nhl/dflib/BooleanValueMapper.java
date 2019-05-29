package com.nhl.dflib;

/**
 * @since 0.6
 */
public interface BooleanValueMapper<V> {

    static BooleanValueMapper<Object> fromObject() {
        return o -> {

            if (o instanceof Boolean) {
                return ((Boolean) o).booleanValue();
            }

            String s = o != null ? o.toString() : null;

            // null-safe... "parseBoolean" returns false for null
            return Boolean.parseBoolean(s);
        };
    }

    static BooleanValueMapper<String> fromString() {
        // null-safe... "parseBoolean" returns false for null
        return s -> Boolean.parseBoolean(s);
    }

    boolean map(V v);
}
