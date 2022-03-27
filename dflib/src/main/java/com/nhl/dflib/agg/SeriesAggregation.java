package com.nhl.dflib.agg;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

/**
 * @since 0.11
 */
public class SeriesAggregation {

    public static DataFrame aggAsDataFrame(Series<?> series, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        Series<?>[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {
            aggColumns[i] = aggregators[i].eval(series);
            aggLabels[i] = aggregators[i].getColumnName();
        }

        return DataFrame.newFrame(Index.forLabelsDeduplicate(aggLabels)).columns(aggColumns);
    }

    public static <T, R> Series<R> aggGroupBy(SeriesGroupBy<T> groupBy, Exp<R> aggregator) {

        Accumulator columnBuilder = Accumulator.factory(aggregator.getType(),groupBy.size());

        for (Object key : groupBy.getGroups()) {
            Series<T> group = groupBy.getGroup(key);
            columnBuilder.add(aggregator.eval(group).get(0));
        }

        return columnBuilder.toSeries();
    }

    public static <T> DataFrame aggGroupMultiple(SeriesGroupBy<T> groupBy, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        int aggH = groupBy.size();

        Series[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {

            Exp<?> agg = aggregators[i];

            Accumulator columnBuilder = Accumulator.factory(agg.getType(),aggH);

            for (Object key : groupBy.getGroups()) {
                Series<T> group = groupBy.getGroup(key);
                columnBuilder.add(agg.eval(group).get(0));
            }

            aggColumns[i] = columnBuilder.toSeries();
            aggLabels[i] = agg.getColumnName();
        }

        return DataFrame.newFrame(Index.forLabelsDeduplicate(aggLabels)).columns(aggColumns);
    }
}
