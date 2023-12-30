package com.nhl.dflib.builder;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.series.LongArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive long values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link com.nhl.dflib.LongSeries}.
 *
 * @since 0.6
 */
public class LongAccum implements ValueAccum<Long> {

    private long[] data;
    private int size;

    public LongAccum() {
        this(10);
    }

    public LongAccum(int capacity) {
        this.size = 0;
        this.data = new long[capacity];
    }

    /**
     * @since 1.0.0-M19
     */
    public void fill(LongSeries values, int valuesOffset, int accumOffset, int len) {
        values.copyToLong(data, valuesOffset, accumOffset, len);
    }

    public void fill(int from, int to, long value) {
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
    public void push(Long v) {
        pushLong(v != null ? v : 0L);
    }

    @Override
    public void pushLong(long value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, Long v) {
        replaceLong(pos, v != null ? v : 0L);
    }

    @Override
    public void replaceLong(int pos, long value) {

        if (pos >= size) {
            size = pos + 1;
        }

        if (size >= data.length) {
            expand(Math.max(data.length * 2, size));
        }

        data[pos] = value;
    }

    @Override
    public LongSeries toSeries() {
        long[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new LongArraySeries(data);
    }

    @Override
    public int size() {
        return size;
    }

    private long[] compactData() {
        if (data.length == size) {
            return data;
        }

        long[] newData = new long[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {
        long[] newData = new long[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
