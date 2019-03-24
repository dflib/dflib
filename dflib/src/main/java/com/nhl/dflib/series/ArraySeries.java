package com.nhl.dflib.series;

import com.nhl.dflib.Series;

public class ArraySeries<T> implements Series<T> {

    private T[] data;

    public ArraySeries(T[] data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public T get(int index) {
        return data[index];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        return this;
    }
}
