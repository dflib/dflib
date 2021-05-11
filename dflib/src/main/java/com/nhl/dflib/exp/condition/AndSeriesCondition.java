package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.SeriesCondition;

/**
 * @since 0.11
 */
public class AndSeriesCondition extends ConjunctiveSeriesCondition {

    public AndSeriesCondition(SeriesCondition... parts) {
        super("and", parts, BooleanSeries::andAll);
    }

    @Override
    public SeriesCondition and(SeriesCondition exp) {
        // flatten AND
        return exp.getClass().equals(AndSeriesCondition.class)
                ? new AndSeriesCondition(combine(this.parts, ((AndSeriesCondition) exp).parts))
                : new AndSeriesCondition(combine(this.parts, exp));
    }

    // TODO: an optimized version of "firstMatch" that does partial evaluation of the parts
}
