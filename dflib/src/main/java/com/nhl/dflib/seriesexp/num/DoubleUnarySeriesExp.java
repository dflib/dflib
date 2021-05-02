package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class DoubleUnarySeriesExp<F> extends UnarySeriesExp<F, Double> implements NumericSeriesExp<Double> {

    public DoubleUnarySeriesExp(SeriesExp<F> exp, Function<Series<F>, Series<Double>> op) {
        super(exp, Double.class, op);
    }
}
