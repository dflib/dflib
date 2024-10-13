package org.dflib.f;

import java.util.Objects;

/**
 * @since 1.1.1
 */
// There's no Float analog for java.util.function.DoublePredicate, so providing it on the DFLib side
@FunctionalInterface
public interface FloatPredicate {

    boolean test(float value);

    default FloatPredicate and(FloatPredicate other) {
        Objects.requireNonNull(other);
        return (value) -> test(value) && other.test(value);
    }

    default FloatPredicate negate() {
        return (value) -> !test(value);
    }

    default FloatPredicate or(FloatPredicate other) {
        Objects.requireNonNull(other);
        return (value) -> test(value) || other.test(value);
    }
}
