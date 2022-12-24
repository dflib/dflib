package com.nhl.dflib.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.series.SingleValueSeries;
import com.nhl.dflib.window.WindowRange;

/**
 * @since 0.14
 */
public class WindowMapper {

    public static <T> Series<T> map(DataFrame df, Exp<T> aggregator, WindowRange range) {

        if (range.alwaysInRange(df.height())) {
            Series<T> oneValSeries = aggregator.eval(df);

            // expand the column to the height of the original DataFrame
            int h = df.height();

            // TODO: primitive series support
            return new SingleValueSeries<>(oneValSeries.get(0), h);
        } else {

            int h = df.height();
            ObjectAccumulator<T> data = new ObjectAccumulator<>(h);
            for (int i = 0; i < h; i++) {
                // TODO: recreating DataFrame for every row.. A hot spot?
                data.push(aggregator.eval(range.selectRows(df, i)).get(0));
            }

            return data.toSeries();
        }
    }

    public static <T> Series<T> mapPartitioned(GroupBy windowGroupBy, Exp<T> aggregator, WindowRange range) {

        int h = windowGroupBy.getUngrouped().height();

        ObjectAccumulator<T> data = new ObjectAccumulator<>(h);

        for (Object key : windowGroupBy.getGroups()) {

            DataFrame gdf = windowGroupBy.getGroup(key);
            Series<T> groupData = map(gdf, aggregator, range);

            IntSeries index = windowGroupBy.getGroupIndex(key);
            int ih = index.size();

            // fill positions in the index with the singe aggregated value
            for (int j = 0; j < ih; j++) {
                data.replace(index.getInt(j), groupData.get(j));
            }
        }

        return data.toSeries();
    }

}
