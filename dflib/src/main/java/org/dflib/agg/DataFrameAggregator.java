package org.dflib.agg;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Defines aggregation operations over DataFrames.
 */
public class DataFrameAggregator {

    public static Series<?>[] agg(DataFrame df, Exp<?>... aggregators) {

        int aggW = aggregators.length;

        Series<?>[] aggColumns = new Series[aggW];

        Environment env = Environment.commonEnv();

        // 1. don't parallelize single-column DataFrames
        // 2. don't parallelize small DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        if (aggW <= 1 || df.height() < env.parallelExecThreshold()) {
            for (int i = 0; i < aggW; i++) {
                aggColumns[i] = new SingleValueSeries(aggregators[i].reduce(df), 1);
            }
        } else {
            ExecutorService pool = env.threadPool();
            Future<Series<?>>[] aggTasks = new Future[aggW];

            for (int i = 0; i < aggW; i++) {
                Exp<?> aggregator = aggregators[i];
                aggTasks[i] = pool.submit(() -> new SingleValueSeries(aggregator.reduce(df), 1));
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
