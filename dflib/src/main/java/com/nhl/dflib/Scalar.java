package com.nhl.dflib;

import com.nhl.dflib.row.RowBuilder;
import com.nhl.dflib.row.RowProxy;

/**
 * A helper class that allows to define type-safe DataFrame value accessors
 *
 * @deprecated since 0.11 not relevant, as we are moving to the {@link Exp} based expressions.
 */
@Deprecated
public class Scalar<T> {

    private int position;

    protected Scalar(int position) {
        this.position = position;
    }

    public static <T> Scalar<T> at(int position) {
        return new Scalar<>(position);
    }

    public static <T, E extends Enum<E>> Scalar<T> at(E e) {
        return new Scalar<>(e.ordinal());
    }

    public T get(Object[] row) {
        return (T) row[position];
    }

    public void set(Object[] row, T value) {
        row[position] = value;
    }

    public T get(RowProxy row) {
        return (T) row.get(position);
    }

    public void set(RowBuilder row, T value) {
        row.set(position, value);
    }

    public Hasher hasher() {
        return Hasher.forColumn(position);
    }

    public Exp<T> first() {
        return Aggregator.first(position);
    }

    /**
     * @since 0.6
     */
    public Exp<Long> countLong() {
        return Aggregator.countLong();
    }

    /**
     * @since 0.6
     */
    public Exp<Integer> countInt() {
        return Aggregator.countInt();
    }

    /**
     * @since 0.6
     */
    public Exp<Double> averageDouble() {
        return Aggregator.averageDouble(position);
    }

    /**
     * @since 0.6
     */
    public Exp<Double> medianDouble() {
        return Aggregator.medianDouble(position);
    }

    /**
     * @since 0.6
     */
    public Exp<Long> sumLong() {
        return Aggregator.sumLong(position);
    }

    /**
     * @since 0.6
     */
    public Exp<Double> sumDouble() {
        return Aggregator.sumDouble(position);
    }

    public int getPosition() {
        return position;
    }
}
