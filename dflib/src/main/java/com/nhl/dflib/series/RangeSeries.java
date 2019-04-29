package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.range.Range;

public class RangeSeries<T> extends ObjectSeries<T> {

    private Series<T> delegate;
    private int from;
    private int size;

    public RangeSeries(Series<T> delegate, int from, int size) {
        this.delegate = delegate;
        this.from = from;
        this.size = size;
        Range.checkRange(from, size, delegate.size());
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return delegate.get(from + index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {

        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        delegate.copyTo(to, this.from + fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
        Object[] range = new Object[size];
        delegate.copyTo(range, this.from, 0, size);
        return new ArraySeries<>((T[]) range);
    }

    @Override
    public Series<T> fillNulls(T value) {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        // TODO: optimize data copy - do materialize and null checking over the same array
        return materialize().fillNullsForward();
    }
}
