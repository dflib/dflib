package com.nhl.dflib;

import com.nhl.dflib.aggregate.ColumnAggregator;
import com.nhl.dflib.row.RowBuilder;
import com.nhl.dflib.row.RowProxy;

/**
 * A helper class that allows to define type-safe DataFrame value accessors
 */
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

    public ColumnAggregator first() {
        return Aggregator.first(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #countLong()}
     */
    @Deprecated
    public ColumnAggregator count() {
        return countLong();
    }

    /**
     * @return a new column aggregator for this position
     * @since 0.6
     */
    public ColumnAggregator countLong() {
        return Aggregator.countLong(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble()}
     */
    @Deprecated
    public ColumnAggregator average() {
        return averageDouble();
    }

    /**
     * @since 0.6
     */
    public ColumnAggregator averageDouble() {
        return Aggregator.averageDouble(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble()}
     */
    @Deprecated
    public ColumnAggregator median() {
        return medianDouble();
    }

    /**
     * @since 0.6
     */
    public ColumnAggregator medianDouble() {
        return Aggregator.medianDouble(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong()}
     */
    @Deprecated
    public ColumnAggregator sum() {
        return sumLong();
    }

    /**
     * @since 0.6
     */
    public ColumnAggregator sumLong() {
        return Aggregator.sumLong(position);
    }

    /**
     * @since 0.6
     */
    public ColumnAggregator sumDouble() {
        return Aggregator.sumDouble(position);
    }

    public int getPosition() {
        return position;
    }
}
