package com.nhl.dflib.map;

/**
 * @since 0.6
 */
@FunctionalInterface
public interface DoubleValueMapper<V> {

    static DoubleValueMapper<String> fromString() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive double");
            }

            return Integer.parseInt(s);
        };
    }

    static DoubleValueMapper<String> fromString(double forNull) {
        return s -> s != null && s.length() > 0 ? Double.parseDouble(s) : forNull;
    }

    static DoubleValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.doubleValue() : 0;
    }

    double map(V v);
}
