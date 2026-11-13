package org.dflib.exp.num;

import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Resolves a concrete numeric type of an "unknown number" expression result at eval time, so that the operation can
 * be delegated to one of the existing type-specific {@link NumericExpFactory} implementations. Mixed numeric types
 * within a single Series are not supported.
 */
final class NumberTypeResolver {

    private NumberTypeResolver() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static ResolvedNumExp<?> resolve(Series<?> series) {
        Class<?> type = series.getInferredType();

        if (type.isPrimitive()) {
            type = boxed(type);
        }

        if (type == Number.class) {
            throw new IllegalArgumentException(
                    "Mixed numeric types in a Series are not supported by 'castAsNumber'. Use an explicit cast instead");
        }

        if (NumericExpFactory.factories.containsKey(type)) {
            return new ResolvedNumExp(type, series);
        }

        // non-numeric values (e.g. Strings), an empty or all-null Series, or a Number subclass without a factory
        return new ResolvedNumExp<>(BigDecimal.class, series.map(NumberTypeResolver::toBigDecimal));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static ResolvedNumExp<?> resolve(Object value) {
        if (value != null && NumericExpFactory.factories.containsKey(value.getClass())) {
            return new ResolvedNumExp(value.getClass(), Series.ofVal(value, 1));
        }

        return new ResolvedNumExp<>(BigDecimal.class, Series.ofVal(toBigDecimal(value), 1));
    }

    private static Class<?> boxed(Class<?> primitive) {
        if (primitive == Integer.TYPE) {
            return Integer.class;
        } else if (primitive == Long.TYPE) {
            return Long.class;
        } else if (primitive == Double.TYPE) {
            return Double.class;
        } else if (primitive == Float.TYPE) {
            return Float.class;
        } else {
            return primitive;
        }
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal bd) {
            return bd;
        } else if (value instanceof BigInteger bi) {
            return new BigDecimal(bi);
        } else if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return BigDecimal.valueOf(((Number) value).longValue());
        } else if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue()).stripTrailingZeros();
        } else {
            // note, here's a hidden validation that will throw for anything that is not-a-number
            return new BigDecimal(value.toString());
        }
    }
}
