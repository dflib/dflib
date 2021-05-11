package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.UnarySeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class DoubleUnaryExp<F> extends UnarySeriesExp<F, Double> implements NumericExp<Double> {

    public DoubleUnaryExp(SeriesExp<F> exp, Function<Series<F>, Series<Double>> op) {
        super(exp, Double.class, op);
    }
}
