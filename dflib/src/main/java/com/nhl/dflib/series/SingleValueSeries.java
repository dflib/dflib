package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Arrays;

/**
 * @param <T>
 * @since 0.6
 */
public class SingleValueSeries<T> extends ObjectSeries<T> {

    private T value;
    private int size;

    public SingleValueSeries(T value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return value;
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        Arrays.fill(to, toOffset, toOffset + len, value);
    }

    @Override
    public Series<T> materialize() {
        return this;
    }

    @Override
    public Series<T> rangeOpenClosed(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new EmptySeries<>();
        }

        if (fromInclusive == 0 && toExclusive == size) {
            return this;
        }

        RangeSeries.checkRanges(fromInclusive, toExclusive - fromInclusive, size);
        return new SingleValueSeries<>(this.value, toExclusive - fromInclusive);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return this.value == null ? new SingleValueSeries<>(value, size) : this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<T> fillNullsForward() {
        return this;
    }
}
