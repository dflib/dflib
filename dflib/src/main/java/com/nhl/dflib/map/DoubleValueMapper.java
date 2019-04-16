package com.nhl.dflib.map;

/**
 * @since 0.6
 */
@FunctionalInterface
public interface DoubleValueMapper<V> {

    static DoubleValueMapper<String> stringToDouble() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive double");
            }

            return Integer.parseInt(s);
        };
    }

    static DoubleValueMapper<String> stringToDouble(int forNull) {
        return s -> s != null && s.length() > 0 ? Double.parseDouble(s) : forNull;
    }

    static DoubleValueMapper<? extends Number> numToDouble() {
        return n -> n != null ? n.doubleValue() : 0;
    }

    double map(V v);
}
