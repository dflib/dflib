package com.nhl.dflib.series.builder;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.series.BooleanArraySeries;

import java.util.Arrays;

/**
 * @since 0.6
 */
public class BooleanAccumulator {

    // TODO: bitmap?
    private boolean[] data;
    private int size;

    public BooleanAccumulator() {
        this(10);
    }

    public BooleanAccumulator(int capacity) {
        this.size = 0;
        this.data = new boolean[capacity];
    }

    public void fill(int from, int to, boolean value) {

        if (to - from < 1) {
            return;
        }

        if (data.length <= to) {
            expand(to);
        }

        Arrays.fill(data, from, to, value);
        size += to - from;
    }

    public void add(boolean value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    public BooleanSeries toBooleanSeries() {
        boolean[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new BooleanArraySeries(data, 0, size);
    }

    public int size() {
        return size;
    }

    private boolean[] compactData() {
        if (data.length == size) {
            return data;
        }

        boolean[] newData = new boolean[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {

        boolean[] newData = new boolean[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
