package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Arrays;

/**
 * @param <T> Series element type
 * @since 0.19
 */
public class ArrayRangeSeries<T> extends ObjectSeries<T> {

    private final T[] data;
    private final int offset;
    private final int size;

    public ArrayRangeSeries(Class<?> nominalType, T[] data, int offset, int size) {
        super(nominalType);
        this.data = data;
        this.offset = offset;
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

        return data[offset + index];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
    }

    @Override
    public Series<T> materialize() {

        if (offset == 0 && size == data.length) {
            return new ArraySeries(data);
        }

        Object[] data = new Object[size];
        copyTo(data, 0, 0, size);
        return new ArraySeries(data);
    }

    @Override
    public Series<T> fillNulls(T value) {

        T[] copy = null;

        for (int i = 0; i < size; i++) {
            if (data[offset + i] == null) {

                if (copy == null) {
                    copy = (T[]) new Object[size];
                    System.arraycopy(data, offset, copy, 0, size);
                }

                copy[i] = value;
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        T[] copy = null;

        for (int i = 0; i < size; i++) {
            if (data[offset + i] == null) {

                if (copy == null) {
                    copy = (T[]) new Object[size];
                    System.arraycopy(data, offset, copy, 0, size);
                }

                copy[i] = values.get(i);
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        T[] copy = null;
        int fillFrom = -1;

        for (int i = 0; i < size; i++) {
            if (data[offset + i] == null) {

                if (copy == null) {
                    copy = (T[]) new Object[size];
                    System.arraycopy(data, offset, copy, 0, size);
                }

                if (fillFrom < 0) {
                    fillFrom = i;
                }
            } else if (fillFrom >= 0) {
                Arrays.fill(copy, fillFrom, i, data[offset + i]);
                fillFrom = -1;
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsForward() {
        T[] copy = null;

        for (int i = 0; i < size; i++) {
            if (data[offset + i] == null) {

                // leading nulls are fine
                if (i == 0) {
                    continue;
                }

                if (copy == null) {
                    copy = (T[]) new Object[size];
                    System.arraycopy(data, offset, copy, 0, size);
                }

                copy[i] = copy[i - 1];
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> rangeOpenClosed(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new ArrayRangeSeries<>(getNominalType(), data, fromInclusive, toExclusive - fromInclusive);
    }
}
