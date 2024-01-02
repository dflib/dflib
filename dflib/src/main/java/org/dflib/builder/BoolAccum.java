package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.dflib.series.BooleanArraySeries;

import java.util.Arrays;

/**
 * @since 0.16
 */
public class BoolAccum implements ValueAccum<Boolean> {

    // TODO: bitmap?
    private boolean[] data;
    private int size;

    public BoolAccum() {
        this(10);
    }

    public BoolAccum(int capacity) {
        this.size = 0;
        this.data = new boolean[capacity];
    }

    /**
     * @since 1.0.0-M19
     */
    public void fill(BooleanSeries values, int valuesOffset, int accumOffset, int len) {
        values.copyToBool(data, valuesOffset, accumOffset, len);
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
        pushBool(v != null ? v : false);
    }

    /**
     * @since 0.16
     */
    @Override
    public void pushBool(boolean value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, Boolean v) {
        replaceBool(pos, v != null ? v : false);
    }

    @Override
    public void replaceBool(int pos, boolean value) {

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

        return new BooleanArraySeries(data);
    }

    @Override
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
