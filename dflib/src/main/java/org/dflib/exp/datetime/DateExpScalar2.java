package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExpScalar2;

import java.time.LocalDate;
import java.util.function.BiFunction;


public class DateExpScalar2<L, R> extends MapExpScalar2<L, R, LocalDate> implements DateExp {

    public static <L, R> DateExpScalar2<L, R> mapVal(String opName, Exp<L> left, R right, BiFunction<L, R, LocalDate> op) {
        return new DateExpScalar2<>(opName, left, right, valToSeries(op));
    }

    public DateExpScalar2(String opName, Exp<L> left, R right, BiFunction<Series<L>, R, Series<LocalDate>> op) {
        super(opName, LocalDate.class, left, right, op);
    }
}
