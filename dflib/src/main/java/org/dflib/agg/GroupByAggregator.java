package org.dflib.agg;

import org.dflib.Exp;
import org.dflib.GroupBy;
import org.dflib.Series;
import org.dflib.exp.ExpEvaluator;

/**
 * @deprecated in favor of {@link ExpEvaluator#reduce(GroupBy, Exp[])}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class GroupByAggregator {

    /**
     * @deprecated in favor of {@link ExpEvaluator#reduce(GroupBy, Exp[])}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<?>[] agg(GroupBy groupBy, Exp<?>... aggregators) {
        return ExpEvaluator.reduce(groupBy, aggregators);
    }
}
