package org.dflib;

import org.dflib.map.AnyToPrimitiveMappers;
import org.dflib.map.NumToPrimitiveMappers;
import org.dflib.map.StringToPrimitiveMappers;

@FunctionalInterface
public interface IntValueMapper<V> {

    /**
     * @since 2.0.0
     */
    static IntValueMapper<Object> of() {
        return AnyToPrimitiveMappers::toInt;
    }

    /**
     * @since 2.0.0
     */
    static IntValueMapper<Object> of(int forNull) {
        return o -> AnyToPrimitiveMappers.toInt(o, forNull);
    }

    /**
     * @since 2.0.0
     */
    static IntValueMapper<String> ofStr() {
        return StringToPrimitiveMappers::toInt;
    }

    /**
     * @since 2.0.0
     */
    static IntValueMapper<String> ofStr(int forNull) {
        return s -> StringToPrimitiveMappers.toInt(s, forNull);
    }

    /**
     * @since 2.0.0
     */
    static IntValueMapper<Number> ofNum() {
        return NumToPrimitiveMappers::toInt;
    }

    /**
     * @since 2.0.0
     */
    static IntValueMapper<Number> ofNum(int forNull) {
        return n -> NumToPrimitiveMappers.toInt(n, forNull);
    }

    /**
     * @deprecated in favor of {@link #of()}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static IntValueMapper<Object> fromObject() {
        return of();
    }

    /**
     * @deprecated in favor of {@link #of(int)}.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static IntValueMapper<Object> fromObject(int forNull) {
        return of(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofStr()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static IntValueMapper<String> fromString() {
        return ofStr();
    }

    /**
     * @deprecated in favor of {@link #ofStr(int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static IntValueMapper<String> fromString(int forNull) {
        return ofStr(forNull);
    }

    /**
     * @deprecated in favor of {@link #ofNum(int)} with "0" as the argument
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    static IntValueMapper<? extends Number> fromNumber() {
        return n -> n != null ? n.intValue() : 0;
    }

    int map(V v);
}
