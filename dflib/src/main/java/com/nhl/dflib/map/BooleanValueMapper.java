package com.nhl.dflib.map;

/**
 * @since 0.6
 */
public interface BooleanValueMapper<V> {

    static BooleanValueMapper<String> stringToBoolean() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive int");
            }

            return Boolean.parseBoolean(s);
        };
    }

    static BooleanValueMapper<String> stringToBoolean(boolean forNull) {
        return s -> s != null && s.length() > 0 ? Boolean.parseBoolean(s) : forNull;
    }

    boolean map(V v);
}
