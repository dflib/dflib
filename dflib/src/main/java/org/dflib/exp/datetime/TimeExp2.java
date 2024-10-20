package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.TimeExp;
import org.dflib.exp.map.MapExp2;

import java.time.LocalTime;
import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class TimeExp2<L, R> extends MapExp2<L, R, LocalTime> implements TimeExp {

    public static <L, R> TimeExp2<L, R> mapVal(String opName, Exp<L> left, Exp<R> right, BiFunction<L, R, LocalTime> op) {
        return new TimeExp2<>(opName, left, right, valToSeries(op));
    }

    public TimeExp2(String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<LocalTime>> op) {
        super(opName, LocalTime.class, left, right, op);
    }
}
