package org.dflib;

import org.dflib.map.AnyToPrimitiveMappers;
import org.dflib.map.NumToPrimitiveMappers;
import org.dflib.map.StringToPrimitiveMappers;

/**
 * @since 1.1.0
 */
@FunctionalInterface
public interface FloatValueMapper<V> {

    /**
     * @since 2.0.0
     */
    static FloatValueMapper<Object> of() {
        return AnyToPrimitiveMappers::toFloat;
    }

    /**
     * @since 2.0.0
     */
    static FloatValueMapper<Object> of(float forNull) {
        return o -> AnyToPrimitiveMappers.toFloat(o, forNull);
    }

    /**
     * @since 2.0.0
     */
    static FloatValueMapper<String> ofStr() {
        return StringToPrimitiveMappers::toFloat;
    }

    /**
     * @since 2.0.0
     */
    static FloatValueMapper<String> ofStr(float forNull) {
        return s -> StringToPrimitiveMappers.toFloat(s, forNull);
    }

    /**
     * @since 2.0.0
     */
    static FloatValueMapper<Number> ofNum() {
        return NumToPrimitiveMappers::toFloat;
    }

    /**
     * @since 2.0.0
     */
    static FloatValueMapper<Number> ofNum(float forNull) {
        return n -> NumToPrimitiveMappers.toFloat(n, forNull);
    }

    /**
     * @deprecated in favor of {@link #of()}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static FloatValueMapper<Object> fromObject() {

        // keeping the old implementation unchanged instead of delegation to "of()" due to some differences

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

    /**
     * @deprecated in favor of {@link #of(float)}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static FloatValueMapper<Object> fromObject(float forNull) {
        return of(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofStr()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static FloatValueMapper<String> fromString() {
        return ofStr();
    }

    /**
     * @deprecated in favor of {@link #ofStr(float)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static FloatValueMapper<String> fromString(float forNull) {
        return ofStr(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofNum(float)} with "0" as the argument
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static FloatValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.floatValue() : 0;
    }

    float map(V v);
}
