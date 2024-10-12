package org.dflib;

import org.dflib.row.RowProxy;

import java.util.function.Predicate;

@FunctionalInterface
public interface RowPredicate {


    static <V> RowPredicate of(int pos, Predicate<V> columnPredicate) {
        return r -> columnPredicate.test((V) r.get(pos));
    }


    static <V> RowPredicate of(String columnName, Predicate<V> columnPredicate) {
        return r -> columnPredicate.test((V) r.get(columnName));
    }

    boolean test(RowProxy r);


    default <V> RowPredicate and(RowPredicate another) {
        return r -> this.test(r) && another.test(r);
    }

    default <V> RowPredicate and(int pos, Predicate<V> another) {
        return r -> this.test(r) && another.test((V) r.get(pos));
    }

    default <V> RowPredicate and(String label, Predicate<V> another) {
        return r -> this.test(r) && another.test((V) r.get(label));
    }

    default <V> RowPredicate or(int pos, Predicate<V> another) {
        return r -> this.test(r) || another.test((V) r.get(pos));
    }

    default <V> RowPredicate or(String label, Predicate<V> another) {
        return r -> this.test(r) || another.test((V) r.get(label));
    }

    default <V> RowPredicate negate() {
        return r -> !this.test(r);
    }
}
