package com.nhl.dflib.series;

import com.nhl.dflib.Series;

public class IntSeries implements Series<Integer> {

    // data.length can be >= size
    private int[] data;
    private int size;

    public IntSeries(int[] data, int size) {
        this.data = data;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    public int getInt(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return data[index];
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    public void copyTo(int[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len >= size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len >= size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public Series<Integer> materialize() {
        // TODO: trim data[] to size?
        return this;
    }

    @Override
    public Series<Integer> fillNulls(Integer value) {
        // assuming there are no nulls ... zeros are not nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsBackwards() {
        // assuming there are no nulls ... zeros are not nulls
        return this;
    }

    @Override
    public Series<Integer> fillNullsForward() {
        // assuming there are no nulls ... zeros are not nulls
        return this;
    }
}
