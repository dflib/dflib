package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.time.LocalDateTime;
import java.util.function.Function;

public class DateTimeExp1<F> extends MapExp1<F, LocalDateTime> implements DateTimeExp {

    public static <F> DateTimeExp1<F> mapVal(String opName, Exp<F> exp, Function<F, LocalDateTime> op) {
        return new DateTimeExp1<>(opName, exp, valToSeries(op));
    }

    public DateTimeExp1(String opName, Exp<F> exp, Function<Series<F>, Series<LocalDateTime>> op) {
        super(opName, LocalDateTime.class, exp, op);
    }
}
