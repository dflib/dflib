package com.nhl.dflib;

import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.EmptySeries;
import com.nhl.dflib.series.RangeSeries;

public interface Series<T> {

    static Series<?>[] fromColumnarData(Object[][] columnarData) {

        int w = columnarData.length;

        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(columnarData[i]);
        }

        return series;
    }

    int size();

    T get(int index);

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
}
