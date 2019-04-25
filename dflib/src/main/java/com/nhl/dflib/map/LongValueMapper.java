package com.nhl.dflib.map;

/**
 * @param <V>
 * @since 0.6
 */
@FunctionalInterface
public interface LongValueMapper<V> {

    static LongValueMapper<Object> fromObject() {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).longValue();
            }

            String s = o != null ? o.toString() : null;
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive long");
            }

            return Long.parseLong(s);
        };
    }

    static LongValueMapper<Object> fromObject(long forNull) {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).longValue();
            }

            String s = o != null ? o.toString() : null;
            return s != null && s.length() > 0 ? Long.parseLong(s) : forNull;
        };
    }

    static LongValueMapper<String> fromString() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive long");
            }

            return Long.parseLong(s);
        };
    }

    static LongValueMapper<String> fromString(long forNull) {
        return s -> s != null && s.length() > 0 ? Long.parseLong(s) : forNull;
    }

    static LongValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.longValue() : 0;
    }

    long map(V v);
}
