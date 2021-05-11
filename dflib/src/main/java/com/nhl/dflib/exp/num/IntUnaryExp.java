package com.nhl.dflib.exp.num;

import com.nhl.dflib.NumericExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.exp.UnarySeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class IntUnaryExp<F> extends UnarySeriesExp<F, Integer> implements NumericExp<Integer> {

    public IntUnaryExp(SeriesExp<F> exp, Function<Series<F>, Series<Integer>> op) {
        super(exp, Integer.class, op);
    }
}
