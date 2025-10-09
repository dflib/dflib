package org.dflib.exp.agg;

import org.dflib.Series;
import org.dflib.agg.CumSum;
import org.dflib.agg.Min;
import org.dflib.agg.Sum;


/**
 * @since 1.1.0
 * @deprecated in favor of individual aggregating operations like {@link Sum}, {@link Min}, etc.
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class BoolAggregators {

    public static Series<Integer> cumSum(Series<Boolean> s) {
        return CumSum.ofBools(s);
    }
}
