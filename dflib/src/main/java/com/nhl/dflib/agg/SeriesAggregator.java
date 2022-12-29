package com.nhl.dflib.agg;

import com.nhl.dflib.*;
import com.nhl.dflib.builder.ValueAccum;
import com.nhl.dflib.builder.ObjectAccum;

/**
 * @since 0.14
 */
public class SeriesAggregator {

    public static DataFrame aggAsDataFrame(Series<?> series, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        Series<?>[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {
            aggColumns[i] = aggregators[i].eval(series);
            aggLabels[i] = aggregators[i].getColumnName();
        }

        return DataFrame.byColumn(Index.forLabelsDeduplicate(aggLabels)).array(aggColumns);
    }

    public static <T, R> Series<R> aggGroupBy(SeriesGroupBy<T> groupBy, Exp<R> aggregator) {

        // TODO: let Aggregator generate and fill SeriesBuilder, as it can use primitive collections
        ObjectAccum<R> columnBuilder = new ObjectAccum<>(groupBy.size());

        for (Object key : groupBy.getGroups()) {
            Series<T> group = groupBy.getGroup(key);
            columnBuilder.push(aggregator.eval(group).get(0));
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

            // TODO: let Aggregator fill Accumulator, as it can use primitive collections
            ValueAccum columnBuilder = new ObjectAccum(aggH);

            for (Object key : groupBy.getGroups()) {
                Series<T> group = groupBy.getGroup(key);
                columnBuilder.push(agg.eval(group).get(0));
            }

            aggColumns[i] = columnBuilder.toSeries();
            aggLabels[i] = agg.getColumnName();
        }

        return DataFrame.byColumn(Index.forLabelsDeduplicate(aggLabels)).array(aggColumns);
    }
}
