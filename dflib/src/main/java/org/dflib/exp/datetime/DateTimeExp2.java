package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class DateTimeExp2<L, R> extends MapExp2<L, R, LocalDateTime> implements DateTimeExp {

    public static <L, R> DateTimeExp2<L, R> mapVal(String opName, Exp<L> left, Exp<R> right, BiFunction<L, R, LocalDateTime> op) {
        return new DateTimeExp2<>(opName, left, right, valToSeries(op));
    }

    public DateTimeExp2(String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<LocalDateTime>> op) {
        super(opName, LocalDateTime.class, left, right, op);
    }
}
