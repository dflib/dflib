package org.dflib.ql;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.exp.ScalarExp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A description of a function argument or a declared parameter: its type and whether it is (or must be) a constant.
 */
record QLFunctionArg(TypeClassifier type, boolean constant, Class<?> numType) {

    // widening only goes to the types that can represent every value of the narrower one exactly; an argument whose
    // value type is unknown at resolve time is cast to the parameter type regardless.
    private static final Map<Class<?>, NumCast> NUM_CASTS = Map.of(
            Integer.class, new NumCast(
                    e -> e.castAsNumber().castAsInt(),
                    Set.of()),
            Long.class, new NumCast(
                    e -> e.castAsNumber().castAsLong(),
                    Set.of(Integer.class)),
            Float.class, new NumCast(
                    e -> e.castAsNumber().castAsFloat(),
                    Set.of()),
            Double.class, new NumCast(
                    e -> e.castAsNumber().castAsDouble(),
                    Set.of(Integer.class, Float.class)),
            BigInteger.class, new NumCast(
                    e -> e.castAsNumber().castAsBigint(),
                    Set.of(Integer.class, Long.class)),
            BigDecimal.class, new NumCast(
                    e -> e.castAsNumber().castAsDecimal(),
                    Set.of(Integer.class, Long.class, Float.class, Double.class, BigInteger.class)));

    QLFunctionArg(TypeClassifier type, boolean constant) {
        this(type, constant, null);
    }

    static QLFunctionArg of(Exp<?> exp) {
        TypeClassifier type = TypeClassifier.classify(exp);
        Class<?> numType = type == TypeClassifier.NUMERIC ? TypeClassifier.numericValueType(exp.getType()) : null;

        // BigDecimal is the only numeric value type with a dedicated interface a parameter can be declared with, so
        // the value type is only recognized on that interface. Any other BigDecimal expression counts as an unknown
        // numeric and is cast with "castAsDecimal", that wraps it in a DecimalExp
        if (numType == BigDecimal.class && !(exp instanceof DecimalExp)) {
            numType = null;
        }

        return new QLFunctionArg(type, exp instanceof ScalarExp, numType);
    }

    /**
     * Returns the cost of passing the argument to a declared parameter or {@link TypeClassifier#NO_MATCH}.
     */
    static int matchCost(QLFunctionArg declared, QLFunctionArg actual) {

        int cost = TypeClassifier.matchCost(declared.type(), actual.type());

        // the parameter binds a numeric value type the argument doesn't have: widen the argument, or cast it if its
        // value type is only known at eval time (e.g. "castAsNumber"), the same way an untyped argument is cast
        if (cost == TypeClassifier.EXACT
                && declared.numType() != null
                && declared.numType() != actual.numType()) {

            cost = actual.numType() == null || NUM_CASTS.get(declared.numType()).widenedFrom().contains(actual.numType())
                    ? TypeClassifier.COERCION
                    : TypeClassifier.NO_MATCH;
        }

        if (declared.constant() && (!actual.constant() || cost == TypeClassifier.COERCION)) {
            return TypeClassifier.NO_MATCH;
        }

        return cost;
    }

    /**
     * Returns true if passing the argument to the declared parameter requires a cast.
     */
    static boolean isCoerced(QLFunctionArg declared, QLFunctionArg actual) {
        return matchCost(declared, actual) == TypeClassifier.COERCION;
    }

    /**
     * Casts an argument to the type of this declared parameter.
     */
    Exp<?> cast(Exp<?> exp) {
        return numType != null ? NUM_CASTS.get(numType).cast().apply(exp) : type.cast(exp);
    }

    /**
     * Renders the parameter type without the constant flag, e.g. {@code NUMERIC} or {@code NUMERIC<Integer>}.
     */
    String typeLabel() {
        return numType != null ? type + "<" + numType.getSimpleName() + ">" : type.toString();
    }

    @Override
    public String toString() {
        return constant ? "const " + typeLabel() : typeLabel();
    }

    private record NumCast(Function<Exp<?>, Exp<?>> cast, Set<Class<?>> widenedFrom) {
    }
}
