package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateTimeExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @since 0.16
 */
public class DateTimeExp1<F> extends MapExp1<F, LocalDateTime> implements DateTimeExp {

    public static <F> DateTimeExp1<F> mapVal(String opName, Exp<F> exp, Function<F, LocalDateTime> op) {
        return new DateTimeExp1<>(opName, exp, valToSeries(op));
    }

    public DateTimeExp1(String opName, Exp<F> exp, Function<Series<F>, Series<LocalDateTime>> op) {
        super(opName, LocalDateTime.class, exp, op);
    }
}
