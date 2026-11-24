package org.dflib.ql.fn;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf3;
import org.dflib.ql.TypeClassifier;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.dflib.ql.fn.FunctionUtils.scalar;
import static org.dflib.ql.fn.FunctionUtils.unchecked;

/**
 * QL {@code shift(e, offset, filler)} function.
 *
 * @since 2.0.0
 */
public class Shift3Function implements Udf3<Object, Number, Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Number> offset, Exp<Object> filler) {
        return unchecked(shift(e, scalar(offset, int.class), scalar(filler, Object.class)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Exp<?> shift(Exp<?> e, int offset, Object filler) {

        TypeClassifier receiverType = TypeClassifier.classify(e);
        TypeClassifier fillerType = filler != null
                ? TypeClassifier.classify(filler.getClass())
                : TypeClassifier.OBJECT;

        if (receiverType.isTyped() && fillerType.isTyped() && receiverType != fillerType) {
            throw new IllegalArgumentException("shift() filler of type " + fillerType
                    + " is not compatible with a " + receiverType + " expression: " + e.toQL());
        }

        return e instanceof NumExp n && filler instanceof Number nf
                ? n.shift(offset, fillerOfType(n, nf))
                : ((Exp) e).shift(offset, filler);
    }

    /**
     * Converts a numeric filler to the value type of the expression.
     */
    private static Number fillerOfType(NumExp<?> e, Number filler) {

        Class<?> type = e.getType();
        if (type == null || type.isInstance(filler)) {
            return filler;
        }

        try {
            if (type == Double.class) {
                return filler instanceof Float f ? Double.parseDouble(f.toString()) : filler.doubleValue();
            } else if (type == Float.class) {
                return filler.floatValue();
            } else if (type == Integer.class) {
                return decimal(filler).intValueExact();
            } else if (type == Long.class) {
                return decimal(filler).longValueExact();
            } else if (type == BigInteger.class) {
                return decimal(filler).toBigIntegerExact();
            } else if (type == BigDecimal.class) {
                return decimal(filler);
            } else {
                // an expression of an unknown numeric type, e.g. "castAsNumber()"
                return filler;
            }
        } catch (ArithmeticException | NumberFormatException ex) {
            throw new IllegalArgumentException("shift() filler " + filler + " can not be converted to the "
                    + type.getSimpleName() + " type of the expression: " + e.toQL());
        }
    }

    private static BigDecimal decimal(Number n) {
        return n instanceof BigDecimal bd ? bd : new BigDecimal(n.toString());
    }
}
