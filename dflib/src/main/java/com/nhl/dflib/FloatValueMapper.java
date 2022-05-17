package com.nhl.dflib;

/**
 * @since 0.6
 */
@FunctionalInterface
public interface FloatValueMapper<V> {

    static FloatValueMapper<Object> fromObject() {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).floatValue();
            }

            String s = o != null ? o.toString() : null;
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive float");
            }

            return Float.parseFloat(s);
        };
    }

    static FloatValueMapper<Object> fromObject(float forNull) {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).floatValue();
            }

            String s = o != null ? o.toString() : null;
            return s != null && s.length() > 0 ? Float.parseFloat(s) : forNull;
        };
    }

    static FloatValueMapper<String> fromString() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive double");
            }

            return Float.parseFloat(s);
        };
    }

    static FloatValueMapper<String> fromString(float forNull) {
        return s -> s != null && s.length() > 0 ? Float.parseFloat(s) : forNull;
    }

    static FloatValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.floatValue() : 0;
    }

    float map(V v);
}
