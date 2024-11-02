package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class DateTimeReduceExp1<F> extends ReduceExp1<F, LocalDateTime> implements DateTimeExp {

    public DateTimeReduceExp1(String opName, Exp<F> exp, Function<Series<F>, LocalDateTime> op, Condition filter) {
        super(opName, LocalDateTime.class, exp, op, filter);
    }

    @Override
    public Series<LocalDateTime> eval(Series<?> s) {
        return new SingleValueSeries<>(reduce(s), s.size());
    }

    @Override
    public Series<LocalDateTime> eval(DataFrame df) {
        return new SingleValueSeries<>(reduce(df), df.height());
    }
}
