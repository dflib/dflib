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

    public ColumnAggregator first() {
        return Aggregator.first(position);
    }

    public ColumnAggregator count() {
        return Aggregator.count(position);
    }

    public ColumnAggregator average() {
        return Aggregator.average(position);
    }

    public ColumnAggregator median() {
        return Aggregator.median(position);
    }

    public ColumnAggregator sum() {
        return Aggregator.sum(position);
    }

    public int getPosition() {
        return position;
    }
}
