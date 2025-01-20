package org.dflib;

import org.dflib.map.AnyToPrimitiveMappers;
import org.dflib.map.NumToPrimitiveMappers;
import org.dflib.map.StringToPrimitiveMappers;

@FunctionalInterface
public interface LongValueMapper<V> {

    /**
     * @since 2.0.0
     */
    static LongValueMapper<Object> of() {
        return AnyToPrimitiveMappers::toLong;
    }

    /**
     * @since 2.0.0
     */
    static LongValueMapper<Object> of(long forNull) {
        return o -> AnyToPrimitiveMappers.toLong(o, forNull);
    }

    /**
     * @since 2.0.0
     */
    static LongValueMapper<String> ofStr() {
        return StringToPrimitiveMappers::toLong;
    }

    /**
     * @since 2.0.0
     */
    static LongValueMapper<String> ofStr(long forNull) {
        return s -> StringToPrimitiveMappers.toLong(s, forNull);
    }

    /**
     * @since 2.0.0
     */
    static LongValueMapper<Number> ofNum() {
        return NumToPrimitiveMappers::toLong;
    }

    /**
     * @since 2.0.0
     */
    static LongValueMapper<Number> ofNum(long forNull) {
        return n -> NumToPrimitiveMappers.toLong(n, forNull);
    }

    /**
     * @deprecated in favor of {@link #of()}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static LongValueMapper<Object> fromObject() {
        // keeping the old implementation unchanged instead of delegation to "of()" due to some differences
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

    /**
     * @deprecated in favor of {@link #of(long)}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static LongValueMapper<Object> fromObject(long forNull) {
        return of(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofStr()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static LongValueMapper<String> fromString() {
        return ofStr();
    }

    /**
     * @deprecated in favor of {@link #ofStr(long)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static LongValueMapper<String> fromString(long forNull) {
        return ofStr(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofNum(long)} with "0" as the argument
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static LongValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.longValue() : 0;
    }

    long map(V v);
}
