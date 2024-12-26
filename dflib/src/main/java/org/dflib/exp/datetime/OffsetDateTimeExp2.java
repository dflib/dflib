package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.time.OffsetDateTime;
import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class OffsetDateTimeExp2<L, R> extends MapExp2<L, R, OffsetDateTime> implements OffsetDateTimeExp {

    public static <L, R> OffsetDateTimeExp2<L, R> mapVal(String opName, Exp<L> left, Exp<R> right, BiFunction<L, R, OffsetDateTime> op) {
        return new OffsetDateTimeExp2<>(opName, left, right, valToSeries(op));
    }

    public OffsetDateTimeExp2(String opName, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, Series<OffsetDateTime>> op) {
        super(opName, OffsetDateTime.class, left, right, op);
    }
}
