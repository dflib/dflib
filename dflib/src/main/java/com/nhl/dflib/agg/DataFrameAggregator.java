package com.nhl.dflib.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

/**
 * Defines aggregation operations over DataFrame's
 *
 * @since 0.14
 */
public class DataFrameAggregator {

    public static DataFrame agg(DataFrame dataFrame, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        Series<?>[] aggColumns = new Series[aggW];
        String[] aggLabels = new String[aggW];

        for (int i = 0; i < aggW; i++) {
            aggColumns[i] = aggregators[i].eval(dataFrame);
            aggLabels[i] = aggregators[i].getColumnName(dataFrame);
        }

        return DataFrame.byColumn(Index.forLabelsDeduplicate(aggLabels)).of(aggColumns);
    }


}
