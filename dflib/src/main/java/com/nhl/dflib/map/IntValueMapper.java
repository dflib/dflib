package com.nhl.dflib.map;

/**
 * @param <V>
 * @since 0.6
 */
@FunctionalInterface
public interface IntValueMapper<V> {

    static IntValueMapper<String> stringToInt() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive int");
            }

            return Integer.parseInt(s);
        };
    }

    static IntValueMapper<String> stringToInt(int forNull) {
        return s -> s != null && s.length() > 0 ? Integer.parseInt(s) : forNull;
    }

    static IntValueMapper<? extends Number> numToInt() {
        return n -> n != null ? n.intValue() : 0;
    }

    int map(V v);
}
