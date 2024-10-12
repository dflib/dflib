package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExpScalar2;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public class DateTimeExpScalar2<L, R> extends MapExpScalar2<L, R, LocalDateTime> implements DateTimeExp {

    public static <L, R> DateTimeExpScalar2<L, R> mapVal(String opName, Exp<L> left, R right, BiFunction<L, R, LocalDateTime> op) {
        return new DateTimeExpScalar2<>(opName, left, right, valToSeries(op));
    }

    public DateTimeExpScalar2(String opName, Exp<L> left, R right, BiFunction<Series<L>, R, Series<LocalDateTime>> op) {
        super(opName, LocalDateTime.class, left, right, op);
    }
}
