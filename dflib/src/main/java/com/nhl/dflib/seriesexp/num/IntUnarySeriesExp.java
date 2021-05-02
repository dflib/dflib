package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class IntUnarySeriesExp<F> extends UnarySeriesExp<F, Integer> implements NumericSeriesExp<Integer> {

    public IntUnarySeriesExp(SeriesExp<F> exp, Function<Series<F>, Series<Integer>> op) {
        super(exp, Integer.class, op);
    }
}
