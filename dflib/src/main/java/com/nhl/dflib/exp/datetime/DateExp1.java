package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DateExp1<F> extends MapExp1<F, LocalDate> implements DateExp {

    public static <F> DateExp1<F> mapVal(String opName, Exp<F> exp, Function<F, LocalDate> op) {
        return new DateExp1<>(opName, exp, valToSeries(op));
    }

    public DateExp1(String opName, Exp<F> exp, Function<Series<F>, Series<LocalDate>> op) {
        super(opName, LocalDate.class, exp, op);
    }
}
