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
public class DoubleExpAggregator<S> implements NumericSeriesExp<Double> {

    private final SeriesExp<S> exp;
    private final Function<Series<S>, Double> aggregator;

    public DoubleExpAggregator(SeriesExp<S> exp, Function<Series<S>, Double> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public Series<Double> eval(DataFrame df) {
        // TODO: optimize for primitive series.
        //  E.g. "IntSeries.average()" is faster than "AggregatorFunctions.averageDouble()"
        double val = aggregator.apply(exp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }
}
