package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ScalarExp;
import org.dflib.ql.TypeClassifier;

import java.lang.invoke.MethodType;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Helpers shared by the built-in function classes.
 */
final class FunctionUtils {

    private FunctionUtils() {
    }

    /**
     * Returns the value of a constant function argument, such as the {@code 1} in {@code plusDays(d, 1)}. An integer
     * type ({@code int}, {@code long}) takes only integer constants within its range.
     *
     * @throws IllegalArgumentException if the argument is not a constant of the requested type
     */
    @SuppressWarnings("unchecked")
    static <T> T scalar(Exp<?> arg, Class<T> type) {

        if (!(arg instanceof ScalarExp<?> scalar)) {
            throw new IllegalArgumentException("Not a constant: " + arg.toQL());
        }

        Object value = scalar.reduce((Series<?>) null);

        if (type == int.class || type == Integer.class) {
            return (T) (Integer) integerConstant(value, arg, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue();
        }

        if (type == long.class || type == Long.class) {
            return (T) (Long) integerConstant(value, arg, Long.MIN_VALUE, Long.MAX_VALUE).longValue();
        }

        if (type == double.class || type == Double.class) {
            // a literal like "0.1" is parsed as a Float, whose "doubleValue()" is 0.10000000149011612
            return (T) (Double) switch (value) {
                case Float f when Float.isFinite(f) -> Double.parseDouble(f.toString());
                case Number n -> n.doubleValue();
                case null, default -> throw new IllegalArgumentException("Not a numeric constant: " + arg.toQL());
            };
        }

        Class<?> boxed = type.isPrimitive() ? MethodType.methodType(type).wrap().returnType() : type;
        if (value == null ? type.isPrimitive() : !boxed.isInstance(value)) {
            throw new IllegalArgumentException("Not a " + type.getSimpleName() + " constant: " + arg.toQL());
        }

        return (T) value;
    }

    private static Number integerConstant(Object value, Exp<?> arg, long min, long max) {

        if (!(value instanceof Integer
                || value instanceof Long
                || value instanceof Short
                || value instanceof Byte
                || value instanceof BigInteger)) {
            throw new IllegalArgumentException("Not an integer constant: " + arg.toQL());
        }

        // compare as BigInteger, as "longValue()" of a BigInteger silently drops the high bits
        Number n = (Number) value;
        BigInteger big = n instanceof BigInteger bi ? bi : BigInteger.valueOf(n.longValue());
        if (big.compareTo(BigInteger.valueOf(min)) < 0 || big.compareTo(BigInteger.valueOf(max)) > 0) {
            throw new IllegalArgumentException("Integer constant out of range [" + min + ".." + max + "]: "
                    + arg.toQL());
        }

        return n;
    }

    /**
     * Reports the first argument of a function that dispatches on its type.
     */
    static IllegalArgumentException unsupportedArg(String fn, Exp<?> arg, TypeClassifier... supported) {

        String types = Arrays.stream(supported).map(TypeClassifier::name).collect(Collectors.joining(", "));
        TypeClassifier type = TypeClassifier.classify(arg);

        return new IllegalArgumentException(type.isTyped()
                ? fn + "(): argument 1 of type " + type + " is not one of " + types
                : fn + "(): argument 1 is untyped, cast it explicitly to one of " + types);
    }

    @SuppressWarnings("unchecked")
    static <T> Exp<T> unchecked(Exp<?> exp) {
        return (Exp<T>) exp;
    }
}
