package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.6
 */
public class IntCountAggregator implements SeriesExp<Integer> {

    private final String name;

    public IntCountAggregator(String name) {
        this.name = name;
    }

    @Override
    public Series<Integer> eval(DataFrame df) {
        int val = df.height();

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return name;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
