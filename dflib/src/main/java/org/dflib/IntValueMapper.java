package org.dflib;

@FunctionalInterface
public interface IntValueMapper<V> {

    static IntValueMapper<Object> fromObject() {
        return o -> {

            if (o == null) {
                throw new IllegalArgumentException("Can't convert a null to a primitive int");
            }

            if (o instanceof Number) {
                return ((Number) o).intValue();
            }

            if (o instanceof Boolean) {
                return ((Boolean) o) ? 1 : 0;
            }

            String s = o.toString();
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

            if (o instanceof Boolean) {
                return ((Boolean) o) ? 1 : 0;
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
