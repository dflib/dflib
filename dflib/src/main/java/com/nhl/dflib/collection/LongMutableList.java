package com.nhl.dflib.collection;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.series.LongArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive long values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link com.nhl.dflib.LongSeries}.
 *
 * @since 0.6
 */
public class LongMutableList {

    private long[] data;
    private int size;

    public LongMutableList() {
        this(10);
    }

    public LongMutableList(int capacity) {
        this.size = 0;
        this.data = new long[capacity];
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

    public void add(long value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    public LongSeries toLongSeries() {
        long[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new LongArraySeries(data, 0, size);
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
