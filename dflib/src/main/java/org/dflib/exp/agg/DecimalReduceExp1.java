package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class DecimalReduceExp1<F> extends ReduceExp1<F, BigDecimal> implements DecimalExp {

    public DecimalReduceExp1(String opName, Exp<F> exp, Function<Series<F>, BigDecimal> op, Condition filter) {
        super(opName, BigDecimal.class, exp, op, filter);
    }

    @Override
    public Series<BigDecimal> eval(Series<?> s) {
        return new SingleValueSeries<>(reduce(s), s.size());
    }

    @Override
    public Series<BigDecimal> eval(DataFrame df) {
        return new SingleValueSeries<>(reduce(df), df.height());
    }
}
