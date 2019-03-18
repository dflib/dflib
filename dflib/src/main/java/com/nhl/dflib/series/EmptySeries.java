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
}
