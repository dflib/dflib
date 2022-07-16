package com.nhl.dflib.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.14
 */
public class WindowMapper {

    public static <T> Series<T> mapGrouped(GroupBy groupBy, Exp<T> aggregator) {

        int h = groupBy.size();

        // TODO: primitives support for performance
        Accumulator<T> data = new ObjectAccumulator(h);

        for (Object key : groupBy.getGroups()) {
            DataFrame group = groupBy.getGroup(key);

            // expecting 1-element Series. Unpack and add to the accum
            data.add(aggregator.eval(group).get(0));
        }

        return data.toSeries();
    }

    public static <T> Series<T> map(DataFrame df, Exp<T> aggregator) {

        Series<T> oneValSeries = aggregator.eval(df);

        // expand the column to the height of the original DataFrame
        int h = df.height();

        // TODO: primitive series support
        return new SingleValueSeries<>(oneValSeries.get(0), h);
    }

    public static <T> Series<T> mapPartitioned(GroupBy windowGroupBy, Exp<T> aggregator) {

        Series<T> rowPerGroup = mapGrouped(windowGroupBy, aggregator);
        int h = windowGroupBy.getUngrouped().height();

        ObjectAccumulator<T> data = new ObjectAccumulator<>(h);

        int gi = 0;
        for (Object key : windowGroupBy.getGroups()) {

            IntSeries index = windowGroupBy.getGroupIndex(key);
            int ih = index.size();

            // fill positions in the index with the singe aggregated value
            T val = rowPerGroup.get(gi);
            for (int j = 0; j < ih; j++) {
                data.set(index.getInt(j), val);
            }

            gi++;
        }

        return data.toSeries();
    }
}
