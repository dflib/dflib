package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.ql.QLFunction;
import org.dflib.ql.TypeClassifier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * QL {@code shift(e, offset)} and {@code shift(e, offset, filler)} functions.
 *
 * @since 2.0.0
 */
public class ShiftFunction implements QLFunction {

    public <N extends Number> NumExp<N> call(NumExp<N> e, int offset) {
        return e.shift(offset);
    }

    public <N extends Number> NumExp<N> call(NumExp<N> e, int offset, N filler) {
        return e.shift(offset, fillerOfType(e, filler));
    }

    public StrExp call(StrExp e, int offset) {
        return e.shift(offset);
    }

    public StrExp call(StrExp e, int offset, String filler) {
        return e.shift(offset, filler);
    }

    public DateExp call(DateExp e, int offset) {
        return e.shift(offset);
    }

    public DateExp call(DateExp e, int offset, LocalDate filler) {
        return e.shift(offset, filler);
    }

    public TimeExp call(TimeExp e, int offset) {
        return e.shift(offset);
    }

    public TimeExp call(TimeExp e, int offset, LocalTime filler) {
        return e.shift(offset, filler);
    }

    public DateTimeExp call(DateTimeExp e, int offset) {
        return e.shift(offset);
    }

    public DateTimeExp call(DateTimeExp e, int offset, LocalDateTime filler) {
        return e.shift(offset, filler);
    }

    public OffsetDateTimeExp call(OffsetDateTimeExp e, int offset) {
        return e.shift(offset);
    }

    public OffsetDateTimeExp call(OffsetDateTimeExp e, int offset, OffsetDateTime filler) {
        return e.shift(offset, filler);
    }

    public <T> Exp<T> call(Exp<T> e, int offset) {
        return e.shift(offset);
    }

    // Also catches a typed receiver with a filler of another type, so the filler type is checked here
    @SuppressWarnings("unchecked")
    public <T> Exp<T> call(Exp<T> e, int offset, Object filler) {

        TypeClassifier receiverType = TypeClassifier.classify(e);
        TypeClassifier fillerType = filler != null
                ? TypeClassifier.classify(filler.getClass())
                : TypeClassifier.OBJECT;

        if (receiverType.isTyped() && fillerType.isTyped() && receiverType != fillerType) {
            throw new IllegalArgumentException("shift() filler of type " + fillerType
                    + " is not compatible with a " + receiverType + " expression: " + e.toQL());
        }

        return e.shift(offset, (T) filler);
    }

    /**
     * Converts a numeric filler to the value type of the expression.
     */
    @SuppressWarnings("unchecked")
    private static <N extends Number> N fillerOfType(NumExp<N> e, N filler) {

        Class<?> type = e.getType();
        if (filler == null || type == null || type.isInstance(filler)) {
            return filler;
        }

        try {
            Number converted;
            if (type == Double.class) {
                converted = filler instanceof Float f ? Double.parseDouble(f.toString()) : filler.doubleValue();
            } else if (type == Float.class) {
                converted = filler.floatValue();
            } else if (type == Integer.class) {
                converted = decimal(filler).intValueExact();
            } else if (type == Long.class) {
                converted = decimal(filler).longValueExact();
            } else if (type == BigInteger.class) {
                converted = decimal(filler).toBigIntegerExact();
            } else if (type == BigDecimal.class) {
                converted = decimal(filler);
            } else {
                // an expression of an unknown numeric type, e.g. "castAsNumber()"
                return filler;
            }

            return (N) converted;

        } catch (ArithmeticException | NumberFormatException ex) {
            throw new IllegalArgumentException("shift() filler " + filler + " can not be converted to the "
                    + type.getSimpleName() + " type of the expression: " + e.toQL());
        }
    }

    private static BigDecimal decimal(Number n) {
        return n instanceof BigDecimal bd ? bd : new BigDecimal(n.toString());
    }
}
