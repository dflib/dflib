package com.nhl.dflib;

import com.nhl.dflib.seriesexp.condition.AndSeriesCondition;
import com.nhl.dflib.seriesexp.condition.OrSeriesCondition;

/**
 * A boolean expression.
 *
 * @since 0.11
 */
public interface SeriesCondition extends SeriesExp<Boolean> {

    @Override
    BooleanSeries eval(DataFrame df);

    default SeriesCondition and(SeriesCondition c) {
        return new AndSeriesCondition(this, c);
    }

    default SeriesCondition or(SeriesCondition c) {
        return new OrSeriesCondition(this, c);
    }

    @Override
    default Class<Boolean> getType() {
        return Boolean.class;
    }
}
