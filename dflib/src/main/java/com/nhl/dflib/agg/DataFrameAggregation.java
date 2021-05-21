package com.nhl.dflib.agg;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * Defines aggregation operations over DataFrame's and GroupBy's
 *
 * @since 0.11
 */
public class DataFrameAggregation {

    public static DataFrame aggDataFrame(DataFrame dataFrame, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        Series<?>[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {
            aggColumns[i] = aggregators[i].eval(dataFrame);
            aggLabels[i] = aggregators[i].getColumnName(dataFrame);
        }

        return DataFrame.newFrame(Index.forLabelsDeduplicate(aggLabels)).columns(aggColumns);
    }

    public static DataFrame aggGroupBy(GroupBy groupBy, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        int aggH = groupBy.size();

        Series[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {

            Exp<?> agg = aggregators[i];

            // TODO: primitives support for performance
            Accumulator columnBuilder = new ObjectAccumulator(aggH);

            for (Object key : groupBy.getGroups()) {
                DataFrame group = groupBy.getGroup(key);

                // expecting 1-element Series. Unpack them and add to the accum
                columnBuilder.add(agg.eval(group).get(0));
            }

            aggColumns[i] = columnBuilder.toSeries();
            aggLabels[i] = agg.getColumnName(groupBy.getUngrouped());
        }

        return DataFrame.newFrame(Index.forLabelsDeduplicate(aggLabels)).columns(aggColumns);
    }

    public static DataFrame aggWindow(DataFrame df, Exp<?>... aggregators) {

        DataFrame oneRowDf = df.agg(aggregators);

        // expand each column to the height of the original DataFrame
        int h = df.height();
        int w = oneRowDf.width();
        Series<?>[] expandedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            // TODO: primitive series support
            expandedColumns[i] = new SingleValueSeries<>(oneRowDf.getColumn(i).get(0), h);
        }

        return DataFrame.newFrame(oneRowDf.getColumnsIndex()).columns(expandedColumns);
    }

    public static DataFrame aggPartitionedWindow(GroupBy windowGroupBy, Exp<?>... aggregators) {

        DataFrame rowPerGroupDf = windowGroupBy.agg(aggregators);
        int h = windowGroupBy.getUngrouped().height();
        int aggW = rowPerGroupDf.width();
        int aggH = rowPerGroupDf.height();


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
            columns[i] = Series.forData(data[i]);
        }

        return DataFrame.newFrame(rowPerGroupDf.getColumnsIndex()).columns(columns);
    }
}
