package com.nhl.dflib.series;

import com.nhl.dflib.Series;

public class IntSeries {

    // data.length can be >= size
    private int[] data;
    private int size;

    public IntSeries(int... data) {
        this(data, data.length);
    }

    public IntSeries(int[] data, int size) {
        this.data = data;
        this.size = size;
    }

    public int size() {
        return size;
    }

    public int get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[index];
    }

    public void copyTo(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    public IntSeries concat(IntSeries... other) {
        if (other.length == 0) {
            return this;
        }

        int h = size;
        for (IntSeries s : other) {
            h += s.size;
        }

        int[] data = new int[h];
        copyTo(data, 0, 0, size);

        int offset = size;
        for (IntSeries s : other) {
            int len = s.size();
            s.copyTo(data, 0, offset, len);
            offset += len;
        }

        return new IntSeries(data);
    }

    public Series<Integer> toSeries() {
        Integer[] data = new Integer[size];
        for(int i = 0; i < size; i++) {
            data[i] = this.data[i];
        }
        return new ArraySeries<>(data);
    }
}
