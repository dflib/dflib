package com.nhl.dflib.series;

import com.nhl.dflib.Series;

public class EmptySeries<T> implements Series<T> {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public T get(int index) {
        throw new ArrayIndexOutOfBoundsException(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > 0) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
    }

    @Override
    public Series<T> materialize() {
        return this;
    }

    @Override
    public Series<T> fillNulls(T value) {
        return this;
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return this;
    }

    @Override
    public Series<T> fillNullsForward() {
        return this;
    }
}
