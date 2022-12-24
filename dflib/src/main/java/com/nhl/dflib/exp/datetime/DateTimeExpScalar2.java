package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateTimeExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExpScalar2;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

/**
 * @since 0.16
 */
public class DateTimeExpScalar2<L, R> extends MapExpScalar2<L, R, LocalDateTime> implements DateTimeExp {

    public static <L, R> DateTimeExpScalar2<L, R> mapVal(String opName, Exp<L> left, R right, BiFunction<L, R, LocalDateTime> op) {
        return new DateTimeExpScalar2<>(opName, left, right, valToSeries(op));
    }

    public DateTimeExpScalar2(String opName, Exp<L> left, R right, BiFunction<Series<L>, R, Series<LocalDateTime>> op) {
        super(opName, LocalDateTime.class, left, right, op);
    }
}
