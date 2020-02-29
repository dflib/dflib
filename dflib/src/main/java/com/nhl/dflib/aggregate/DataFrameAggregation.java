package com.nhl.dflib.aggregate;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

/**
 * Defines aggregation operations over DataFrame's and GroupBy's
 *
 * @since 0.6
 */
public class DataFrameAggregation {

    public static Series<?> aggDataFrame(DataFrame dataFrame, Aggregator<?>... aggregators) {

        int aggW = aggregators.length;
        Object[] aggValues = new Object[aggW];

        for (int i = 0; i < aggW; i++) {
            aggValues[i] = aggregators[i].aggregate(dataFrame);
        }

        return new ArraySeries<>(aggValues);
    }

    public static DataFrame aggGroupBy(GroupBy groupBy, Aggregator<?>... aggregators) {

        int aggW = aggregators.length;
        int aggH = groupBy.size();

        Index sourceIndex = groupBy.getUngroupedColumnIndex();

        Series[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {

            Aggregator agg = aggregators[i];

            // TODO: let Aggregator fill Accumulator, as it can use primitive collections
            Accumulator columnBuilder = new ObjectAccumulator(aggH);

            for (Object key : groupBy.getGroups()) {
                DataFrame group = groupBy.getGroup(key);
                columnBuilder.add(agg.aggregate(group));
            }

            aggColumns[i] = columnBuilder.toSeries();
            aggLabels[i] = agg.aggregateLabel(sourceIndex);
        }

        return DataFrame.newFrame(Index.forLabelsDeduplicate(aggLabels)).columns(aggColumns);
    }
}
