package com.nhl.dflib.seriesexp.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class IntExpAggregator<S> implements NumericSeriesExp<Integer> {

    private final SeriesExp<S> exp;
    private final Function<Series<S>, Integer> aggregator;

    public IntExpAggregator(SeriesExp<S> exp, Function<Series<S>, Integer> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Series<Integer> eval(DataFrame df) {

        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        int val = aggregator.apply(exp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        int val = aggregator.apply(exp.eval(s));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName() {
        return exp.getName();
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }
}
