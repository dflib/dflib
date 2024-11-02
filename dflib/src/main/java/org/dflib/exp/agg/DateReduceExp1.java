package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class DateReduceExp1<F> extends ReduceExp1<F, LocalDate> implements DateExp {

    public DateReduceExp1(String opName, Exp<F> exp, Function<Series<F>, LocalDate> op, Condition filter) {
        super(opName, LocalDate.class, exp, op, filter);
    }

    @Override
    public Series<LocalDate> eval(Series<?> s) {
        return new SingleValueSeries<>(reduce(s), s.size());
    }

    @Override
    public Series<LocalDate> eval(DataFrame df) {
        return new SingleValueSeries<>(reduce(df), df.height());
    }
}
