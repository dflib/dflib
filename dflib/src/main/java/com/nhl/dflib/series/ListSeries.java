package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated as it is no longer useful in DFLib internally. Use ArraySeries or any other Series implementation instead.
 */
@Deprecated(since = "0.19", forRemoval = true)
public class ListSeries<T> extends ObjectSeries<T> {

    private final List<T> data;

    public ListSeries(List<T> data) {
        super(Object.class);
        this.data = data;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for (int i = 0; i < len; i++) {
            to[toOffset + i] = data.get(fromOffset + i);
        }
    }

    @Override
    public Series<T> materialize() {
        Object[] copy = new Object[data.size()];
        return new ArraySeries<>((T[]) data.toArray(copy));
    }

    @Override
    public Series<T> fillNulls(T value) {

        int len = data.size();
        T[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data.get(i) == null) {

                if (copy == null) {
                    copy = data.toArray((T[]) new Object[len]);
                }

                copy[i] = value;
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        int len = data.size();
        T[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data.get(i) == null) {

                if (copy == null) {
                    copy = data.toArray((T[]) new Object[len]);
                }

                copy[i] = values.get(i);
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        int len = data.size();
        T[] copy = null;
        int fillFrom = -1;

        for (int i = 0; i < len; i++) {
            if (data.get(i) == null) {

                if (copy == null) {
                    copy = data.toArray((T[]) new Object[len]);
                }

                if (fillFrom < 0) {
                    fillFrom = i;
                }
            } else if (fillFrom >= 0) {
                Arrays.fill(copy, fillFrom, i, data.get(i));
                fillFrom = -1;
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }

    @Override
    public Series<T> fillNullsForward() {
        int len = data.size();
        T[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data.get(i) == null) {

                // leading nulls are fine
                if (i == 0) {
                    continue;
                }

                if (copy == null) {
                    copy = data.toArray((T[]) new Object[len]);
                }

                copy[i] = copy[i - 1];
            }
        }

        return copy != null ? new ArraySeries<>(copy) : this;
    }
}
