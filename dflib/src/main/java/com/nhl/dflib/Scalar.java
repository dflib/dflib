package com.nhl.dflib;

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

    public Aggregator<T> first() {
        return Aggregator.first(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #countLong()}
     */
    @Deprecated
    public Aggregator count() {
        return countLong();
    }

    /**
     * @since 0.6
     */
    public Aggregator<Long> countLong() {
        return Aggregator.countLong();
    }

    /**
     * @since 0.6
     */
    public Aggregator<Integer> countInt() {
        return Aggregator.countInt();
    }

    /**
     * @deprecated since 0.6 in favor of {@link #averageDouble()}
     */
    @Deprecated
    public Aggregator average() {
        return averageDouble();
    }

    /**
     * @since 0.6
     */
    public Aggregator<Double> averageDouble() {
        return Aggregator.averageDouble(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #medianDouble()}
     */
    @Deprecated
    public Aggregator median() {
        return medianDouble();
    }

    /**
     * @since 0.6
     */
    public Aggregator<Double> medianDouble() {
        return Aggregator.medianDouble(position);
    }

    /**
     * @deprecated since 0.6 in favor of {@link #sumLong()}
     */
    @Deprecated
    public Aggregator sum() {
        return sumLong();
    }

    /**
     * @since 0.6
     */
    public Aggregator<Long> sumLong() {
        return Aggregator.sumLong(position);
    }

    /**
     * @since 0.6
     */
    public Aggregator<Double> sumDouble() {
        return Aggregator.sumDouble(position);
    }

    public int getPosition() {
        return position;
    }
}
