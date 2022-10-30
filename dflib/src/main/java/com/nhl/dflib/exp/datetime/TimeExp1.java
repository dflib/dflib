package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.TimeExp;
import com.nhl.dflib.exp.map.MapExp1;

import java.time.LocalTime;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class TimeExp1<F> extends MapExp1<F, LocalTime> implements TimeExp {

    public static <F> TimeExp1<F> mapVal(String opName, Exp<F> exp, Function<F, LocalTime> op) {
        return new TimeExp1<>(opName, exp, valToSeries(op));
    }

    public TimeExp1(String opName, Exp<F> exp, Function<Series<F>, Series<LocalTime>> op) {
        super(opName, LocalTime.class, exp, op);
    }
}
