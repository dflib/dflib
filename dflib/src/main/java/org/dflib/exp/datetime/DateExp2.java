package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.time.LocalDate;
import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class DateExp2<L, R> extends MapExp2<L, R, LocalDate> implements DateExp {

    public static <L, R> DateExp2<L, R> mapVal(String opName, Exp<L> left, Exp<R> right, BiFunction<L, R, LocalDate> op) {
        return new DateExp2<>(opName, left, right, valToSeries(op));
    }

    public DateExp2(String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<LocalDate>> op) {
        super(opName, LocalDate.class, left, right, op);
    }
}
