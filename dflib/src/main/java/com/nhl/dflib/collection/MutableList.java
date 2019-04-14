package com.nhl.dflib.collection;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;

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

    public void add(T value) {

        if (size == data.length) {
            expand();
        }

        data[size++] = value;
    }

    public Series<T> toSeries() {
        T[] data = this.data;

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        // TODO: difference from IntMutableList in that IntArraySeries supports ranged... Reconcile ArraySeries?
        return new ArraySeries<>(data).head(size);
    }

    private void expand() {

        int newCapacity = data.length * 2;
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = (T[]) newData;
    }
}
