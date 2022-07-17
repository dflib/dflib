package com.nhl.dflib.exp.agg;

import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.Exp1;
import com.nhl.dflib.series.SingleValueSeries;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DecimalExpAggregator<F> extends Exp1<F, BigDecimal> implements DecimalExp {

    private final Function<Series<F>, BigDecimal> aggregator;

    public DecimalExpAggregator(String opName, Exp<F> exp, Function<Series<F>, BigDecimal> aggregator) {
        super(opName, BigDecimal.class, exp);
        this.aggregator = aggregator;
    }

    @Override
    protected Series<BigDecimal> doEval(Series<F> s) {
        BigDecimal val = aggregator.apply(s);
        return new SingleValueSeries<>(val, 1);
    }
}
