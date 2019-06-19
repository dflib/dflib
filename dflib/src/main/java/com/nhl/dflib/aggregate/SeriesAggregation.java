package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesAggregator;
import com.nhl.dflib.SeriesGroupBy;
import com.nhl.dflib.series.builder.ObjectAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;

/**
 * @since 0.6
 */
public class SeriesAggregation {

    public static <T, R> Series<R> aggGroupBy(SeriesGroupBy<T> groupBy, SeriesAggregator<? super T, R> aggregator) {

        // TODO: let Aggregator generate and fill SeriesBuilder, as it can use primitive collections
        ObjectAccumulator<R> columnBuilder = new ObjectAccumulator<>(groupBy.size());

        for (Object key : groupBy.getGroups()) {
            Series<T> group = groupBy.getGroup(key);
            columnBuilder.add(aggregator.aggregate(group));
        }

        return columnBuilder.toSeries();
    }

    public static <T> DataFrame aggGroupMultiple(SeriesGroupBy<T> groupBy, SeriesAggregator<? super T, ?>... aggregators) {

        int aggW = aggregators.length;
        int aggH = groupBy.size();

        Series[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {

            SeriesAggregator agg = aggregators[i];

            // TODO: let Aggregator generate and fill SeriesBuilder, as it can use primitive collections
            SeriesBuilder columnBuilder = new ObjectAccumulator(aggH);

            for (Object key : groupBy.getGroups()) {
                Series<T> group = groupBy.getGroup(key);
                columnBuilder.add(agg.aggregate(group));
            }

            aggColumns[i] = columnBuilder.toSeries();
            aggLabels[i] = agg.aggregateLabel();
        }

        return DataFrame.newFrame(Index.forLabelsDeduplicate(aggLabels)).columns(aggColumns);
    }
}
