package com.nhl.dflib.seriesexp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.SeriesCondition;

/**
 * @since 0.11
 */
public class OrSeriesCondition extends ConjunctiveSeriesCondition {

    public OrSeriesCondition(SeriesCondition... parts) {
        super("or", parts, BooleanSeries::orAll);
    }

    @Override
    public SeriesCondition or(SeriesCondition exp) {
        // flatten OR
        return exp.getClass().equals(OrSeriesCondition.class)
                ? new OrSeriesCondition(combine(this.parts, ((OrSeriesCondition) exp).parts))
                : new OrSeriesCondition(combine(this.parts, exp));
    }


}
