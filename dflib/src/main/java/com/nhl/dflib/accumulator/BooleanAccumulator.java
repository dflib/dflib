package com.nhl.dflib.accumulator;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.series.BooleanArraySeries;

import java.util.Arrays;

/**
 * @since 0.6
 */
public class BooleanAccumulator implements ValueAccum<Boolean> {

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

    /**
     * @since 0.8
     */
    @Override
    public void push(Boolean v) {
        pushBoolean(v != null ? v : false);
    }

    /**
     * @since 0.8
     */
    @Override
    public void pushBoolean(boolean value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, Boolean v) {
        replaceBoolean(pos, v != null ? v : false);
    }

    @Override
    public void replaceBoolean(int pos, boolean value) {

        if (pos >= size) {
            size = pos + 1;
        }

        if (size >= data.length) {
            expand(Math.max(data.length * 2, size));
        }

        data[pos] = value;
    }

    @Override
    public BooleanSeries toSeries() {
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
