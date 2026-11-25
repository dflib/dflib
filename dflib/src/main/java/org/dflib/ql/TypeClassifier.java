package org.dflib.ql;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.function.Function;

/**
 * The type of a QL expression as seen by the function resolver: one of the typed expression kinds, or
 * {@link #OBJECT} for an expression whose type is not known statically (an untyped column reference, a "first"
 * or an "if" result, a constant of an unlisted Java type).
 *
 * @since 2.0.0
 */
public enum TypeClassifier {

    NUMERIC(Exp::castAsNumber),
    STRING(Exp::castAsStr),
    BOOLEAN(Exp::castAsBool),
    DATE(Exp::castAsDate),
    TIME(Exp::castAsTime),
    DATETIME(Exp::castAsDateTime),
    OFFSETDATETIME(Exp::castAsOffsetDateTime),
    OBJECT(Function.identity());

    private final Function<Exp<?>, Exp<?>> cast;

    TypeClassifier(Function<Exp<?>, Exp<?>> cast) {
        this.cast = cast;
    }

    /**
     * Returns true for a classifier that names a concrete type, i.e. anything but OBJECT.
     */
    public boolean isTyped() {
        return this != OBJECT;
    }

    Exp<?> cast(Exp<?> exp) {
        return cast.apply(exp);
    }

    /**
     * Classifies an expression by the interface it implements. An expression implementing none of the typed
     * interfaces is an OBJECT, whatever its declared value type.
     */
    public static TypeClassifier classify(Exp<?> exp) {
        return switch (exp) {
            case NumExp<?> ignored -> NUMERIC;
            case StrExp ignored -> STRING;
            case Condition ignored -> BOOLEAN;
            case DateExp ignored -> DATE;
            case TimeExp ignored -> TIME;
            case DateTimeExp ignored -> DATETIME;
            case OffsetDateTimeExp ignored -> OFFSETDATETIME;
            case null, default -> OBJECT;
        };
    }

    /**
     * Classifies a value type, e.g. the {@code A} of a function parameter declared as {@code Exp<A>}.
     */
    public static TypeClassifier classify(Class<?> valueType) {
        if (Number.class.isAssignableFrom(valueType)) {
            return NUMERIC;
        } else if (CharSequence.class.isAssignableFrom(valueType)) {
            return STRING;
        } else if (valueType == Boolean.class) {
            return BOOLEAN;
        } else if (valueType == LocalDate.class) {
            return DATE;
        } else if (valueType == LocalTime.class) {
            return TIME;
        } else if (valueType == LocalDateTime.class) {
            return DATETIME;
        } else if (valueType == OffsetDateTime.class) {
            return OFFSETDATETIME;
        } else {
            return OBJECT;
        }
    }
}
