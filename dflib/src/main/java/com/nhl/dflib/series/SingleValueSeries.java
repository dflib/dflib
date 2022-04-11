package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.range.Range;

import java.util.Arrays;

/**
 * @param <T>
 * @since 0.6
 */
public class SingleValueSeries<T> extends ObjectSeries<T> {

    private T value;
    private int size;

    public SingleValueSeries(T value, int size) {
        super(Object.class);
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
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

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, size);
        return new SingleValueSeries<>(this.value, toExclusive - fromInclusive);
    }

    @Override
    public Series<T> fillNulls(T value) {
        return this.value == null ? new SingleValueSeries<>(value, size) : this;
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        return this.value == null ? alignAndReplace(values) : this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<T> fillNullsForward() {
        return this;
    }

    private Series<T> alignAndReplace(Series<? extends T> another) {

        int as = another.size();
        if (size == as) {
            return (Series<T>) another;
        } else if (size < as) {
            return (Series<T>) another.head(size);
        } else {
            throw new IllegalArgumentException("Another Series is smaller than this size: " + as + " < " + size);
        }
    }
}
