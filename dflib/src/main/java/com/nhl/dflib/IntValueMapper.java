package com.nhl.dflib;

/**
 * @param <V>
 * @since 0.6
 */
@FunctionalInterface
public interface IntValueMapper<V> {

    static IntValueMapper<Object> fromObject() {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).intValue();
            }

            String s = o != null ? o.toString() : null;
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive int");
            }

            return Integer.parseInt(s);
        };
    }

    static IntValueMapper<Object> fromObject(int forNull) {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).intValue();
            }

            String s = o != null ? o.toString() : null;
            return s != null && s.length() > 0 ? Integer.parseInt(s) : forNull;
        };
    }

    static IntValueMapper<String> fromString() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive int");
            }

            return Integer.parseInt(s);
        };
    }

    static IntValueMapper<String> fromString(int forNull) {
        return s -> s != null && s.length() > 0 ? Integer.parseInt(s) : forNull;
    }

    static IntValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.intValue() : 0;
    }

    int map(V v);
}
