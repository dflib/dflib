package com.nhl.dflib.accumulator;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.StringSeries;

import java.util.Arrays;

public class StringAccumulator extends ObjectAccumulator<String> {


    protected String[] data;
    protected int size;

    public StringAccumulator() {
        this(10);
    }

    public StringAccumulator(int capacity) {
        this.size = 0;
        this.data = new String[capacity];
    }

    public void fill(int from, int to, String value) {
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
    public void add(String value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void set(int pos, String v) {
        if (pos >= size) {
            throw new IndexOutOfBoundsException(pos + " is out of bounds for " + size);
        }

        data[pos] = v;
    }

    @Override
    public Series<String> toSeries() {
        String[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        // TODO: difference from IntMutableList in that IntArraySeries supports ranged... Reconcile ArraySeries?
        return new StringSeries(data);
    }

    protected String[] compactData() {
        if (data.length == size) {
            return data;
        }

        String[] newData = new String[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {
        String[] newData = new String[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }


}
