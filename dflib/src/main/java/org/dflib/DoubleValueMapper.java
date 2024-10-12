package org.dflib;

@FunctionalInterface
public interface DoubleValueMapper<V> {

    static DoubleValueMapper<Object> fromObject() {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            }

            String s = o != null ? o.toString() : null;
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive double");
            }

            return Double.parseDouble(s);
        };
    }

    static DoubleValueMapper<Object> fromObject(double forNull) {
        return o -> {

            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            }

            if (o instanceof Boolean) {
                return ((Boolean) o) ? 1. : 0.;
            }

            String s = o != null ? o.toString() : null;
            return s != null && s.length() > 0 ? Double.parseDouble(s) : forNull;
        };
    }

    static DoubleValueMapper<String> fromString() {
        return s -> {
            if (s == null || s.length() == 0) {
                throw new IllegalArgumentException("Can't convert a null to a primitive double");
            }

            return Double.parseDouble(s);
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
