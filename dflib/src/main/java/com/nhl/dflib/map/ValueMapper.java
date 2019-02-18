package com.nhl.dflib.map;

@FunctionalInterface
public interface ValueMapper<V, VR> {

    static ValueMapper<String, Integer> stringToInt() {
        return s -> s != null ? Integer.valueOf(s) : null;
    }

    static ValueMapper<String, String> stringToString() {
        return s -> s;
    }

    static ValueMapper<String, Long> stringToLong() {
        return s -> s != null ? Long.valueOf(s) : null;
    }

    static ValueMapper<String, Double> stringToDouble() {
        return s -> s != null ? Double.valueOf(s) : null;
    }

    VR map(V v);
}
