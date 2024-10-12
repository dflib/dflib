package org.dflib.exp.agg;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.SingleValueSeries;

import java.math.BigDecimal;
import java.util.function.Function;


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
