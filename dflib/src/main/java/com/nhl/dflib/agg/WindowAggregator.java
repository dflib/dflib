package com.nhl.dflib.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.builder.ValueAccum;
import com.nhl.dflib.exec.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @since 0.14
 */
public class WindowAggregator {

    public static DataFrame aggGrouped(GroupBy groupBy, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        int aggH = groupBy.size();

        Series[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        Environment env = Environment.commonEnv();

        // 1. don't parallelize single-column aggregations
        // 2. don't parallelize small DataFrames, as sequential calculations are fast enough vs the overhead of
        // creating, submitting and joining tasks

        if (aggW <= 1 || groupBy.getUngrouped().height() < env.parallelExecThreshold()) {
            for (int i = 0; i < aggW; i++) {
                aggColumns[i] = aggGrouped(groupBy, aggregators[i], aggH);
                aggLabels[i] = aggregators[i].getColumnName(groupBy.getUngrouped());
            }
        } else {
            ExecutorService pool = env.threadPool();
            Future<Series<?>>[] aggTasks = new Future[aggW];

            for (int i = 0; i < aggW; i++) {
                Exp<?> agg = aggregators[i];
                aggTasks[i] = pool.submit(() -> aggGrouped(groupBy, agg, aggH));
                aggLabels[i] = agg.getColumnName(groupBy.getUngrouped());
            }

            for (int i = 0; i < aggW; i++) {
                try {
                    aggColumns[i] = aggTasks[i].get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return DataFrame.byColumn(Index.ofDeduplicated(aggLabels)).of(aggColumns);
    }

    private static Series<?> aggGrouped(GroupBy groupBy, Exp<?> agg, int aggH) {
        // TODO: primitives support for performance
        ValueAccum columnBuilder = new ObjectAccum(aggH);

        for (Object key : groupBy.getGroups()) {
            DataFrame group = groupBy.getGroup(key);

            // expecting 1-element Series. Unpack them and add to the accum
            columnBuilder.push(agg.eval(group).get(0));
        }

        return columnBuilder.toSeries();
    }

    public static DataFrame aggPartitioned(GroupBy windowGroupBy, Exp<?>... aggregators) {

        DataFrame rowPerGroupDf = aggGrouped(windowGroupBy, aggregators);
        int h = windowGroupBy.getUngrouped().height();
        int aggW = rowPerGroupDf.width();

        Object[][] data = new Object[aggW][h];

        for (int i = 0; i < aggW; i++) {
            data[i] = new Object[h];
        }

        int gi = 0;
        for (Object key : windowGroupBy.getGroups()) {

            IntSeries index = windowGroupBy.getGroupIndex(key);
            int ih = index.size();

            for (int i = 0; i < aggW; i++) {

                // fill positions in the index with the singe aggregated value
                Object val = rowPerGroupDf.getColumn(i).get(gi);
                for (int j = 0; j < ih; j++) {
                    data[i][index.getInt(j)] = val;
                }
            }

            gi++;
        }

        Series<?>[] columns = new Series[aggW];
        for (int i = 0; i < aggW; i++) {
            columns[i] = Series.of(data[i]);
        }

        return DataFrame.byColumn(rowPerGroupDf.getColumnsIndex()).of(columns);
    }

    public static DataFrame agg(DataFrame df, Exp<?>... aggregators) {

        DataFrame oneRowDf = df.agg(aggregators);

        // expand each column to the height of the original DataFrame
        int h = df.height();
        int w = oneRowDf.width();
        Series<?>[] expandedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            expandedColumns[i] = Series.ofVal(oneRowDf.getColumn(i).get(0), h);
        }

        return DataFrame.byColumn(oneRowDf.getColumnsIndex()).of(expandedColumns);
    }
}
