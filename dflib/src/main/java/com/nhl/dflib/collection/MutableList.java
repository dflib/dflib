package com.nhl.dflib.collection;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

import java.util.Arrays;

/**
 * @since 0.6
 */
public class MutableList<T> {

    private T[] data;
    private int size;

    public MutableList() {
        this(10);
    }

    public MutableList(int capacity) {
        this.size = 0;
        this.data = (T[]) new Object[capacity];
    }

    public void fill(int from, int to, T value) {
        if (to - from < 1) {
            return;
        }

        if (data.length <= to) {
            expand(to);
        }

        Arrays.fill(data, from, to, value);
        size += to - from;
    }

    public void add(T value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    public Series<T> toSeries() {
        T[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        // TODO: difference from IntMutableList in that IntArraySeries supports ranged... Reconcile ArraySeries?
        return new ArraySeries<>(data);
    }

    private T[] compactData() {
        Object[] newData = new Object[size];
        System.arraycopy(data, 0, newData, 0, size);
        return (T[]) newData;
    }

    private void expand(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = (T[]) newData;
    }
}
