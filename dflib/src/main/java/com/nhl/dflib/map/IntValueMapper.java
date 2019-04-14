package com.nhl.dflib.map;

/**
 * @param <V>
 * @since 0.6
 */
@FunctionalInterface
public interface IntValueMapper<V> {

    static IntValueMapper<String> stringToInt() {
        return s -> s != null ? Integer.parseInt(s) : 0;
    }

    static IntValueMapper<? extends Number> numToInt() {
        return n -> n != null ? n.intValue() : 0;
    }

    int map(V v);
}
