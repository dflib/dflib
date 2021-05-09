package com.nhl.dflib.seriesexp.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DecimalExpAggregator implements NumericSeriesExp<BigDecimal> {

    private final SeriesExp<BigDecimal> exp;
    private final Function<Series<BigDecimal>, BigDecimal> aggregator;

    public DecimalExpAggregator(SeriesExp<BigDecimal> exp, Function<Series<BigDecimal>, BigDecimal> aggregator) {
        this.exp = exp;
        this.aggregator = aggregator;
    }

    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

    @Override
    public Series<BigDecimal> eval(DataFrame df) {
        BigDecimal val = aggregator.apply(exp.eval(df));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public Series<BigDecimal> eval(Series<?> s) {
        BigDecimal val = aggregator.apply(exp.eval(s));
        return new SingleValueSeries<>(val, 1);
    }

    @Override
    public String getName(DataFrame df) {
        return exp.getName(df);
    }
}
