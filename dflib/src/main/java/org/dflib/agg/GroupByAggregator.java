package org.dflib.agg;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.GroupBy;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ValueAccum;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class GroupByAggregator {

    public static Series<?>[] agg(GroupBy groupBy, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        int aggH = groupBy.size();

        Series<?>[] aggColumns = new Series[aggW];

        Environment env = Environment.commonEnv();

        // 1. don't parallelize single-column aggregations
        // 2. don't parallelize small DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        if (aggW <= 1 || groupBy.getSource().height() < env.parallelExecThreshold()) {
            for (int i = 0; i < aggW; i++) {
                aggColumns[i] = agg(groupBy, aggregators[i], aggH);
            }
        } else {
            ExecutorService pool = env.threadPool();
            Future<Series<?>>[] aggTasks = new Future[aggW];

            for (int i = 0; i < aggW; i++) {
                Exp<?> agg = aggregators[i];
                aggTasks[i] = pool.submit(() -> agg(groupBy, agg, aggH));
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

    private static Series<?> agg(GroupBy groupBy, Exp<?> agg, int aggH) {
        // TODO: primitives support for performance
        ValueAccum columnBuilder = new ObjectAccum<>(aggH);

        // if aggH == 0, there will be no group keys, and the result will be empty
        // TODO: a check for aggH == 0 to avoid the implicit assumption?
        for (Object key : groupBy.getGroupKeys()) {
            DataFrame group = groupBy.getGroup(key);

            // expecting 1-element Series. Unpack them and add to the accum
            columnBuilder.push(agg.reduce(group));
        }

        return columnBuilder.toSeries();
    }
}
