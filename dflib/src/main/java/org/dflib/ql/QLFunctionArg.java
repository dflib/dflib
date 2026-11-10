package org.dflib.ql;

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

    // widening only goes to the types that can represent every value of the narrower one exactly
    private static final Map<Class<?>, NumCast> NUM_CASTS = Map.of(
            Integer.class, new NumCast(Exp::castAsInt, Set.of()),
            Long.class, new NumCast(Exp::castAsLong, Set.of(Integer.class)),
            Float.class, new NumCast(Exp::castAsFloat, Set.of()),
            Double.class, new NumCast(Exp::castAsDouble, Set.of(Integer.class, Float.class)),
            BigInteger.class, new NumCast(Exp::castAsBigint, Set.of(Integer.class, Long.class)),
            BigDecimal.class, new NumCast(Exp::castAsDecimal,
                    Set.of(Integer.class, Long.class, Float.class, Double.class, BigInteger.class)));

    QLFunctionArg(TypeClassifier type, boolean constant) {
        this(type, constant, null);
    }

    static QLFunctionArg of(Exp<?> exp) {
        TypeClassifier type = TypeClassifier.classify(exp);
        Class<?> numType = type == TypeClassifier.NUMERIC ? TypeClassifier.numericValueType(exp.getType()) : null;
        return new QLFunctionArg(type, exp instanceof ScalarExp, numType);
    }

    /**
     * Returns the cost of passing the argument to a declared parameter or {@link TypeClassifier#NO_MATCH}.
     */
    static int matchCost(QLFunctionArg declared, QLFunctionArg actual) {

        int cost = TypeClassifier.matchCost(declared.type(), actual.type());

        if (cost == TypeClassifier.EXACT
                && declared.numType() != null
                && actual.numType() != null
                && declared.numType() != actual.numType()) {

            cost = NUM_CASTS.get(declared.numType()).widenedFrom().contains(actual.numType())
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
