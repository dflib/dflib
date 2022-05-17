package com.nhl.dflib.accumulator;

import com.nhl.dflib.FloatSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.FloatArraySeries;
import com.nhl.dflib.series.FloatBaseSeries;

import java.util.Arrays;

public class FloatAccumulator implements Accumulator<Float> {

    private float[] data;
    private int size;

    public FloatAccumulator() {
        this(10);
    }

    public FloatAccumulator(int capacity) {
        this.size = 0;
        this.data = new float[capacity];
    }

    public void fill(int from, int to, float value) {

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
    public void add(Float v) {
        addFloat(v != null ? v : (float) 0.);
    }

    /**
     * @since 0.8
     */
    @Override
    public void addFloat(float value) {
        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void set(int pos, Float v) {
        setFloat(pos, v != null ? v : 0.f);
    }

    @Override
    public void setFloat(int pos, float value) {

        if (pos >= size) {
            throw new IndexOutOfBoundsException(pos + " is out of bounds for " + size);
        }

        data[pos] = value;
    }

    @Override
    public FloatSeries toSeries() {
        float[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new FloatArraySeries(data, 0, size);
    }

    public int size() {
        return size;
    }

    private float[] compactData() {
        if (data.length == size) {
            return data;
        }

        float[] newData = new float[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {
        float[] newData = new float[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        this.data = newData;
    }
}
