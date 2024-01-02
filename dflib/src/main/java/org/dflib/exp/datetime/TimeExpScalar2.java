package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.TimeExp;
import org.dflib.exp.map.MapExpScalar2;

import java.time.LocalTime;
import java.util.function.BiFunction;

/**
 * @since 0.16
 */
public class TimeExpScalar2<L, R> extends MapExpScalar2<L, R, LocalTime> implements TimeExp {

    public static <L, R> TimeExpScalar2<L, R> mapVal(String opName, Exp<L> left, R right, BiFunction<L, R, LocalTime> op) {
        return new TimeExpScalar2<>(opName, left, right, valToSeries(op));
    }

    public TimeExpScalar2(String opName, Exp<L> left, R right, BiFunction<Series<L>, R, Series<LocalTime>> op) {
        super(opName, LocalTime.class, left, right, op);
    }
}
