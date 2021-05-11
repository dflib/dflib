package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
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

    @Override
    public int firstMatch(DataFrame df) {
        int len = parts.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = parts[i].firstMatch(df);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }

    @Override
    public int firstMatch(Series<?> s) {
        int len = parts.length;
        int r = -1;

        // since we are dealing with "or", it is enough to find the minimal first match among all columns
        for (int i = 0; i < len; i++) {
            int rx = parts[i].firstMatch(s);
            r = r < 0 ? rx : Math.min(r, rx);

            if (r == 0) {
                return 0;
            }
        }

        return r;
    }
}
