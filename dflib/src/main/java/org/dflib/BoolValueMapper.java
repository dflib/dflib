package org.dflib;

/**
 * A mapper of some value to a boolean. Used in various data conversion APIs.
 */
public interface BoolValueMapper<V> {

    /**
     * @since 2.0.0
     */
    static BoolValueMapper<Object> of() {
        return o -> {

            if (o instanceof Boolean) {
                return ((Boolean) o).booleanValue();
            }

            if (o instanceof Number) {
                return ((Number) o).intValue() != 0;
            }

            String s = o != null ? o.toString() : null;

            // null-safe... "parseBoolean" returns false for null
            return Boolean.parseBoolean(s);
        };
    }

    /**
     * @since 2.0.0
     */
    static BoolValueMapper<String> ofString() {
        // null-safe... "parseBoolean" returns false for null
        return s -> Boolean.parseBoolean(s);
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
     * @deprecated in favor of {@link #ofString()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static BoolValueMapper<String> fromString() {
        return ofString();
    }

    boolean map(V v);
}
