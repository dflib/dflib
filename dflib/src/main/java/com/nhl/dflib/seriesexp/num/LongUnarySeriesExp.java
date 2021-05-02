package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class LongUnarySeriesExp<F> extends UnarySeriesExp<F, Long> implements NumericSeriesExp<Long> {

    public LongUnarySeriesExp(SeriesExp<F> exp, Function<Series<F>, Series<Long>> op) {
        super(exp, Long.class, op);
    }
}
