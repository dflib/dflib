package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Arrays;

public class ArraySeries<T> extends ObjectSeries<T> {

    private T[] data;

    public ArraySeries(T... data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public T get(int index) {
        return data[index];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        return this;
    }

    @Override
    public Series<T> fillNulls(T value) {

        int len = data.length;
        T[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = (T[]) new Object[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = value;
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        int len = data.length;
        T[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = (T[]) new Object[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = values.get(i);
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        int len = data.length;
        T[] copy = null;
        int fillFrom = -1;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = (T[]) new Object[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                if (fillFrom < 0) {
                    fillFrom = i;
                }
            } else if (fillFrom >= 0) {
                Arrays.fill(copy, fillFrom, i, data[i]);
                fillFrom = -1;
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsForward() {
        int len = data.length;
        T[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                // leading nulls are fine
                if (i == 0) {
                    continue;
                }

                if (copy == null) {
                    copy = (T[]) new Object[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = copy[i - 1];
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }
}
