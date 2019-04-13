package com.nhl.dflib.series;

import com.nhl.dflib.Series;

public class IntSeries {

    // data.length can be >= size
    private int[] data;
    private int offset;
    private int size;

    public IntSeries(int... data) {
        this(data, 0, data.length);
    }

    public IntSeries(int[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }

    public int size() {
        return size;
    }

    public int getInt(int index) {
        if (offset + index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[offset + index];
    }

    public void copyToInt(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, offset + fromOffset, to, toOffset, len);
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
        copyToInt(data, 0, 0, size);

        int offset = size;
        for (IntSeries s : other) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return new IntSeries(data);
    }

    public IntSeries head(int len) {
        return len < size ? new IntSeries(data, offset, len) : this;
    }

    public IntSeries tail(int len) {
        return len < size ? new IntSeries(data, offset + size - len, len) : this;
    }

    public Series<Integer> toSeries() {
        Integer[] data = new Integer[size];
        for(int i = offset; i < size; i++) {
            data[i] = this.data[i];
        }
        return new ArraySeries<>(data);
    }
}
