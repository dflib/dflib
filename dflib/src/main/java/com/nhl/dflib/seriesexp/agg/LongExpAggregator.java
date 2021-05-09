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
public class LongExpAggregator<S> implements NumericSeriesExp<Long> {

    private final SeriesExp<S> exp;
    private final Function<Series<S>, Long> aggregator;

    public LongExpAggregator(SeriesExp<S> exp, Function<Series<S>, Long> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Series<Long> eval(DataFrame df) {

        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        long val = aggregator.apply(exp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public Series<Long> eval(Series<?> s) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"

        long val = aggregator.apply(exp.eval(s));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }
}
