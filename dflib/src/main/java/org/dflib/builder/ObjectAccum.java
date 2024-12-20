package org.dflib.builder;

import org.dflib.Series;
import org.dflib.series.ArraySeries;

import java.util.Arrays;

public class ObjectAccum<T> implements ValueAccum<T> {

    private T[] data;
    private int size;

    public ObjectAccum() {
        this(10);
    }

    public ObjectAccum(int capacity) {
        this.size = 0;
        this.data = (T[]) new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }


    public void fill(Series<T> values, int valuesOffset, int accumOffset, int len) {

        if (len <= 0) {
            return;
        }

        int pastFillEnd = accumOffset + len;
        if (data.length < pastFillEnd) {
            expand(pastFillEnd);
            size = pastFillEnd;
        } else if (size < pastFillEnd) {
            size = pastFillEnd;
        }

        values.copyTo(data, valuesOffset, accumOffset, len);
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

    @Override
    public void push(T value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, T v) {
        if (pos >= size) {
            size = pos + 1;
        }

        if (size >= data.length) {
            expand(Math.max(data.length * 2, size));
        }

        data[pos] = v;
    }

    @Override
    public Series<T> toSeries() {
        T[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        // TODO: difference from IntMutableList in that IntArraySeries supports ranged... Reconcile ArraySeries?
        return new ArraySeries<>(data);
    }

    private T[] compactData() {
        if (data.length == size) {
            return data;
        }

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
