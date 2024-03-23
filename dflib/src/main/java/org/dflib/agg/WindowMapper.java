package org.dflib.agg;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.GroupBy;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.window.WindowRange;

/**
 * @since 0.14
 */
public class WindowMapper {

    public static <T> Series<T> map(DataFrame df, Exp<T> aggregator, WindowRange range) {

        if (range.alwaysInRange(df.height())) {
            Series<T> oneValSeries = aggregator.eval(df);

            // expand the column to the height of the original DataFrame
            int h = df.height();

            return Series.ofVal(oneValSeries.get(0), h);
        } else {

            int h = df.height();
            ObjectAccum<T> data = new ObjectAccum<>(h);
            for (int i = 0; i < h; i++) {
                // TODO: recreating DataFrame for every row.. A hot spot?
                data.push(aggregator.eval(range.selectRows(df, i)).get(0));
            }

            return data.toSeries();
        }
    }

    public static <T> Series<T> mapPartitioned(GroupBy windowGroupBy, Exp<T> aggregator, WindowRange range) {

        int h = windowGroupBy.getSource().height();

        ObjectAccum<T> data = new ObjectAccum<>(h);

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
