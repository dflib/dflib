package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.time.LocalDate;
import java.util.function.Function;


public class DateExp1<F> extends MapExp1<F, LocalDate> implements DateExp {

    public static <F> DateExp1<F> mapVal(String opName, Exp<F> exp, Function<F, LocalDate> op) {
        return new DateExp1<>(opName, exp, valToSeries(op));
    }

    public DateExp1(String opName, Exp<F> exp, Function<Series<F>, Series<LocalDate>> op) {
        super(opName, LocalDate.class, exp, op);
    }
}
