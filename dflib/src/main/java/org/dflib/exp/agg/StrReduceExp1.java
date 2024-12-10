package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.series.SingleValueSeries;

import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class StrReduceExp1<F> extends ReduceExp1<F, String> implements StrExp {

    public StrReduceExp1(String opName, Exp<F> exp, Function<Series<F>, String> op, Condition filter) {
        super(opName, String.class, exp, op, filter);
    }

    @Override
    public Series<String> eval(Series<?> s) {
        return new SingleValueSeries<>(reduce(s), s.size());
    }

    @Override
    public Series<String> eval(DataFrame df) {
        return new SingleValueSeries<>(reduce(df), df.height());
    }
}
