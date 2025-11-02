package org.dflib.agg;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpEvaluator;

/**
 * Defines aggregation operations over DataFrames.
 *
 * @deprecated in favor of {@link ExpEvaluator#reduce(DataFrame, Exp[])}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class DataFrameAggregator {

    /**
     * @deprecated in favor of {@link ExpEvaluator#reduce(DataFrame, Exp[])}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Series<?>[] agg(DataFrame df, Exp<?>... aggregators) {
        return ExpEvaluator.reduce(df, aggregators);
    }
}
