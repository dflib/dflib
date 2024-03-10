package org.dflib.agg;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Defines aggregation operations over DataFrames.
 *
 * @since 0.14
 */
public class DataFrameAggregator {

    /**
     * @since 1.0.0-M20
     */
    public static Series<?>[] agg(DataFrame df, Exp<?>... aggregators) {

        int aggW = aggregators.length;

        Series<?>[] aggColumns = new Series[aggW];

        Environment env = Environment.commonEnv();

        // 1. don't parallelize single-column DataFrames
        // 2. don't parallelize small DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        if (aggW <= 1 || df.height() < env.parallelExecThreshold()) {
            for (int i = 0; i < aggW; i++) {
                aggColumns[i] = aggregators[i].eval(df);
            }
        } else {
            ExecutorService pool = env.threadPool();
            Future<Series<?>>[] aggTasks = new Future[aggW];

            for (int i = 0; i < aggW; i++) {
                Exp<?> aggregator = aggregators[i];
                aggTasks[i] = pool.submit(() -> aggregator.eval(df));
            }

            for (int i = 0; i < aggW; i++) {
                try {
                    aggColumns[i] = aggTasks[i].get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return aggColumns;
    }
}
