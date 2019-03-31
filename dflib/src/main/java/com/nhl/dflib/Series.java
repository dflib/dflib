package com.nhl.dflib;

import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.EmptySeries;
import com.nhl.dflib.series.RangeSeries;

public interface Series<T> {

    static <T> Series<T> forData(T... data) {
        return new ArraySeries<>(data);
    }

    int size();

    T get(int index);

    void copyTo(Object[] to, int fromOffset, int toOffset, int len);

    /**
     * Returns a {@link Series} that contains a range of data from this series. If the "toExclusive" parameter
     *
     * @param fromInclusive a left boundary index of the returned range (included in the returned range)
     * @param toExclusive   a right boundary index (excluded in the returned range)
     * @return a Series that contains a sub-range of data from this Series.
     */
    default Series<T> openClosedRange(int fromInclusive, int toExclusive) {

        if (fromInclusive == toExclusive) {
            return new EmptySeries<>();
        }

        return fromInclusive == 0 && toExclusive == size()
                ? this
                // RangeSeries does range checking
                : new RangeSeries<>(this, fromInclusive, toExclusive - fromInclusive);
    }

    Series<T> materialize();

    Series<T> fillNulls(T value);

    Series<T> fillNullsBackwards();

    Series<T> fillNullsForward();

    default Series<T> concat(Series<? extends T>... other) {
        if (other.length == 0) {
            return this;
        }

        Series<? extends T>[] combined = new Series[other.length + 1];
        combined[0] = this;
        System.arraycopy(other, 0, combined, 1, other.length);

        return SeriesConcat.concat(combined);
    }
}
