package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.6
 * @deprecated since 0.11 as it is no longer needed
 */
@Deprecated
public class LongCountAggregator implements Exp<Long> {

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Series<Long> eval(DataFrame df) {
        long val = df.height();

        // TODO: LongSingleValueSeries
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public Series<Long> eval(Series<?> s) {
        long val = s.size();

        // TODO: LongSingleValueSeries
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName() {
        return "countLong";
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
