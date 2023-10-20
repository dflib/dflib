package com.nhl.dflib;

import com.nhl.dflib.row.RowProxy;

@FunctionalInterface
public interface RowPredicate {

    /**
     * @deprecated in favor of {@link #of(int, ValuePredicate)}
     */
    @Deprecated(since = "0.16", forRemoval = true)
    static <V> RowPredicate forColumn(int pos, ValuePredicate<V> columnPredicate) {
        return of(pos, columnPredicate);
    }

    /**
     * @since 0.16
     */
    static <V> RowPredicate of(int pos, ValuePredicate<V> columnPredicate) {
        return r -> columnPredicate.test((V) r.get(pos));
    }

    /**
     * @since 0.16
     */
    static <V> RowPredicate of(String columnName, ValuePredicate<V> columnPredicate) {
        return r -> columnPredicate.test((V) r.get(columnName));
    }

    boolean test(RowProxy r);

    /**
     * @since 0.7
     */
    default <V> RowPredicate and(RowPredicate another) {
        return r -> this.test(r) && another.test(r);
    }

    default <V> RowPredicate and(int pos, ValuePredicate<V> another) {
        return r -> this.test(r) && another.test((V) r.get(pos));
    }

    default <V> RowPredicate and(String label, ValuePredicate<V> another) {
        return r -> this.test(r) && another.test((V) r.get(label));
    }

    default <V> RowPredicate or(int pos, ValuePredicate<V> another) {
        return r -> this.test(r) || another.test((V) r.get(pos));
    }

    default <V> RowPredicate or(String label, ValuePredicate<V> another) {
        return r -> this.test(r) || another.test((V) r.get(label));
    }

    default <V> RowPredicate negate() {
        return r -> !this.test(r);
    }
}
