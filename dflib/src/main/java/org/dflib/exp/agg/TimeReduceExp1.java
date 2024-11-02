package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.TimeExp;
import org.dflib.series.SingleValueSeries;

import java.time.LocalTime;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class TimeReduceExp1<F> extends ReduceExp1<F, LocalTime> implements TimeExp {

    public TimeReduceExp1(String opName, Exp<F> exp, Function<Series<F>, LocalTime> op, Condition filter) {
        super(opName, LocalTime.class, exp, op, filter);
    }

    @Override
    public Series<LocalTime> eval(Series<?> s) {
        return new SingleValueSeries<>(reduce(s), s.size());
    }

    @Override
    public Series<LocalTime> eval(DataFrame df) {
        return new SingleValueSeries<>(reduce(df), df.height());
    }
}
