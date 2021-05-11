package com.nhl.dflib;

import com.nhl.dflib.exp.condition.AndSeriesCondition;
import com.nhl.dflib.exp.condition.OrSeriesCondition;

/**
 * A {@link SeriesExp} that evaluates to a BooleanSeries indicating whether the condition is true for any given
 * row of the source DataFrame.
 *
 * @since 0.11
 */
public interface SeriesCondition extends SeriesExp<Boolean> {

    @Override
    BooleanSeries eval(DataFrame df);

    @Override
    BooleanSeries eval(Series<?> s);

    default int firstMatch(DataFrame df) {
        return eval(df).firstTrue();
    }

    default int firstMatch(Series<?> s) {
        return eval(s).firstTrue();
    }

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
