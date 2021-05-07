package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.6
 */
public class LongCountAggregator implements SeriesExp<Long> {

    private final String name;

    public LongCountAggregator(String name) {
        this.name = name;
    }

    @Override
    public Series<Long> eval(DataFrame df) {
        long val = df.height();

        // TODO: LongSingleValueSeries
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return name;
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }
}
