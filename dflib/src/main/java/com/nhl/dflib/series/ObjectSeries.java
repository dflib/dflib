package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.concat.SeriesConcat;

import static java.util.Arrays.asList;

public abstract class ObjectSeries<T> implements Series<T> {

    @Override
    public Series<T> rangeOpenClosed(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new EmptySeries<>();
        }

        return fromInclusive == 0 && toExclusive == size()
                ? this
                // RangeSeries does range checking
                : new RangeSeries<>(this, fromInclusive, toExclusive - fromInclusive);
    }

    @Override
    public Series<T> concat(Series<? extends T>... other) {
        if (other.length == 0) {
            return this;
        }

        Series<T>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(asList(combined));
    }

    @Override
    public Series<T> head(int len) {
        return len >= size() ? this : rangeOpenClosed(0, len);
    }

    @Override
    public Series<T> tail(int len) {
        int size = size();
        return len >= size ? this : rangeOpenClosed(size - len, size);
    }
}
