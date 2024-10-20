package org.dflib.exp.agg;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp1;
import org.dflib.series.SingleValueSeries;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class DecimalReduceExp1<F> extends Exp1<F, BigDecimal> implements DecimalExp {

    private final Function<Series<F>, BigDecimal> op;

    public DecimalReduceExp1(String opName, Exp<F> exp, Function<Series<F>, BigDecimal> op) {
        super(opName, BigDecimal.class, exp);
        this.op = op;
    }

    @Override
    protected Series<BigDecimal> doEval(Series<F> s) {
        BigDecimal val = op.apply(s);
        return new SingleValueSeries<>(val, 1);
    }
}
