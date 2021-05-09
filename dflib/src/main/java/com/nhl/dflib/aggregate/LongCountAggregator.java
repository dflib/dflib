package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.6
 * @deprecated since 0.11 as it is no longer needed
 */
@Deprecated
public class LongCountAggregator implements SeriesExp<Long> {

    @Override
    public Series<Long> eval(DataFrame df) {
        long val = df.height();

        // TODO: LongSingleValueSeries
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public Series<Long> eval(Series<?> s) {
        // do not expect to be called on this deprecated class
        throw new UnsupportedOperationException("Unsupported eval with Series... The class is deprecated, consider switching to Exp API");
    }

    @Override
    public String getName(DataFrame df) {
        return "countLong";
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }
}
