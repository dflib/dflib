package org.dflib.agg;


import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ValueAccum;
import org.dflib.exp.Exps;

public class SeriesAggregator {

    public static DataFrame aggAsDataFrame(Series<?> series, Exp<?>... aggregators) {

        int aggW = aggregators.length;
        Series<?>[] aggColumns = new Series[aggW];

        for (int i = 0; i < aggW; i++) {
            aggColumns[i] = aggregators[i].eval(series);
        }

        return DataFrame.byColumn(Exps.index(aggregators)).of(aggColumns);
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

        Series<?>[] aggColumns = new Series[aggW];

        for (int i = 0; i < aggW; i++) {

            Exp<?> agg = aggregators[i];

            // TODO: let Aggregator fill Accumulator, as it can use primitive collections
            ValueAccum columnBuilder = new ObjectAccum<>(aggH);

            for (Object key : groupBy.getGroups()) {
                Series<T> group = groupBy.getGroup(key);
                columnBuilder.push(agg.eval(group).get(0));
            }

            aggColumns[i] = columnBuilder.toSeries();
        }

        return DataFrame.byColumn(Exps.index(aggregators)).of(aggColumns);
    }
}
