package com.nhl.dflib.series.builder;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.series.LongArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive long values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link com.nhl.dflib.LongSeries}.
 *
 * @since 0.6
 */
public class LongAccumulator {

    private long[] data;
    private int size;

    public LongAccumulator() {
        this(10);
    }

    public LongAccumulator(int capacity) {
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

    public void set(int pos, long value) {

        if (pos >= size) {
            throw new IndexOutOfBoundsException(pos + " is out of bounds for " + size);
        }

        data[pos] = value;
    }

    /**
     * @since 0.8
     */
    public long peek() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty accumulator");
        }

        return data[size - 1];
    }

    /**
     * @since 0.8
     */
    public void pop() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty accumulator");
        }

        size--;
    }

    public LongSeries toLongSeries() {
        long[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new LongArraySeries(data, 0, size);
    }

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
