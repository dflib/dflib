package com.nhl.dflib;

/**
 * @since 0.6
 */
public interface BoolValueMapper<V> {

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

    static BoolValueMapper<String> fromString() {
        // null-safe... "parseBoolean" returns false for null
        return s -> Boolean.parseBoolean(s);
    }

    boolean map(V v);
}
