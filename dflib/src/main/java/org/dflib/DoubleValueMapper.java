package org.dflib;

import org.dflib.map.AnyToPrimitiveMappers;
import org.dflib.map.NumToPrimitiveMappers;
import org.dflib.map.StringToPrimitiveMappers;

@FunctionalInterface
public interface DoubleValueMapper<V> {

    /**
     * @since 2.0.0
     */
    static DoubleValueMapper<Object> of() {
        return AnyToPrimitiveMappers::toDouble;
    }

    /**
     * @since 2.0.0
     */
    static DoubleValueMapper<Object> of(double forNull) {
        return o -> AnyToPrimitiveMappers.toDouble(o, forNull);
    }

    /**
     * @since 2.0.0
     */
    static DoubleValueMapper<String> ofStr() {
        return StringToPrimitiveMappers::toDouble;
    }

    /**
     * @since 2.0.0
     */
    static DoubleValueMapper<String> ofStr(double forNull) {
        return s -> StringToPrimitiveMappers.toDouble(s, forNull);
    }

    /**
     * @since 2.0.0
     */
    static DoubleValueMapper<Number> ofNum() {
        return NumToPrimitiveMappers::toDouble;
    }

    /**
     * @since 2.0.0
     */
    static DoubleValueMapper<Number> ofNum(double forNull) {
        return n -> NumToPrimitiveMappers.toDouble(n, forNull);
    }

    /**
     * @deprecated in favor of {@link #of()}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static DoubleValueMapper<Object> fromObject() {

        // keeping the old implementation unchanged instead of delegation to "of()" due to some differences

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

    /**
     * @deprecated in favor of {@link #of(double)}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static DoubleValueMapper<Object> fromObject(double forNull) {
        return of(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofStr()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static DoubleValueMapper<String> fromString() {
        return ofStr();
    }

    /**
     * @deprecated in favor of {@link #ofNum(double)} with "0" as the argument
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static DoubleValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.doubleValue() : 0;
    }

    double map(V v);
}
