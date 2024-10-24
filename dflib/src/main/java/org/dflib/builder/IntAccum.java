package org.dflib.builder;

import org.dflib.IntSeries;
import org.dflib.series.IntArraySeries;

import java.util.Arrays;

/**
 * An expandable list of primitive int values that has minimal overhead and can be converted to compact and efficient
 * immutable {@link IntSeries}.
 */
public class IntAccum implements ValueAccum<Integer> {

    private int[] data;
    private int size;

    public IntAccum() {
        this(10);
    }

    public IntAccum(int capacity) {
        this.size = 0;
        this.data = new int[capacity];
    }


    public void fill(IntSeries values, int valuesOffset, int accumOffset, int len) {
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

        values.copyToInt(data, valuesOffset, accumOffset, len);
    }

    public void fill(int from, int to, int value) {
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
    public void push(Integer v) {
        pushInt(v != null ? v : 0);
    }

    @Override
    public void pushInt(int value) {

        if (size == data.length) {
            expand(data.length * 2);
        }

        data[size++] = value;
    }

    @Override
    public void replace(int pos, Integer v) {
        replaceInt(pos, v != null ? v : 0);
    }

    @Override
    public void replaceInt(int pos, int value) {

        if (pos >= size) {
            size = pos + 1;
        }

        if (size >= data.length) {
            expand(Math.max(data.length * 2, size));
        }

        data[pos] = value;
    }

    public int[] toArray() {
        int[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return data;
    }

    @Override
    public IntSeries toSeries() {
        int[] data = compactData();

        // making sure no one can change the series via the Mutable List anymore
        this.data = null;

        return new IntArraySeries(data);
    }

    @Override
    public int size() {
        return size;
    }

    private int[] compactData() {
        if (data.length == size) {
            return data;
        }

        int[] newData = new int[size];
        System.arraycopy(data, 0, newData, 0, size);
        return newData;
    }

    private void expand(int newCapacity) {
        int[] newData = new int[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);

        this.data = newData;
    }
}
