package org.dflib;

import java.util.function.Predicate;

/**
 * @deprecated in favor of the JDK {@link java.util.function.Predicate}
 */
@Deprecated(since = "1.0.0-M21", forRemoval = true)
@FunctionalInterface
public interface ValuePredicate<V> extends Predicate<V> {

    static <V> ValuePredicate<V> isIn(V... values) {
        Predicate<V> p = Predicates.isIn(values);
        return p::test;
    }

    static <V> ValuePredicate<V> isIn(Iterable<? extends V> values) {
        Predicate<V> p = Predicates.isIn(values);
        return p::test;
    }

    default ValuePredicate<V> and(ValuePredicate<? super V> another) {
        return v -> this.test(v) && another.test(v);
    }

    default ValuePredicate<V> or(ValuePredicate<? super V> another) {
        return v -> this.test(v) || another.test(v);
    }

    default ValuePredicate<V> negate() {
        return v -> !this.test(v);
    }
}
