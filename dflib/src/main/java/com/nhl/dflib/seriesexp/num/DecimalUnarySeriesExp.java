package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.NumericSeriesExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.seriesexp.UnarySeriesExp;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DecimalUnarySeriesExp<F> extends UnarySeriesExp<F, BigDecimal> implements NumericSeriesExp<BigDecimal> {

    public DecimalUnarySeriesExp(SeriesExp<F> exp, Function<Series<F>, Series<BigDecimal>> op) {
        super(exp, BigDecimal.class, op);
    }
}
