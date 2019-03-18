package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Objects;

public class HeadSeries<T> implements Series<T> {

    private Series<T> source;
    private int len;

    public HeadSeries(Series<T> source, int len) {
        this.source = Objects.requireNonNull(source);

        int maxLen = source.size();
        this.len = len > maxLen ? maxLen : len;
    }

    @Override
    public int size() {
        return len;
    }

    @Override
    public T get(int index) {

        if (index >= len) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return source.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > this.len) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        source.copyTo(to, fromOffset, toOffset, len);
    }
}
