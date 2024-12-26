package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExpScalar2;

import java.time.OffsetDateTime;
import java.util.function.BiFunction;

/**
 * @since 1.1.0
 */
public class OffsetDateTimeExpScalar2<L, R> extends MapExpScalar2<L, R, OffsetDateTime> implements OffsetDateTimeExp {

    public static <L, R> OffsetDateTimeExpScalar2<L, R> mapVal(String opName, Exp<L> left, R right, BiFunction<L, R, OffsetDateTime> op) {
        return new OffsetDateTimeExpScalar2<>(opName, left, right, valToSeries(op));
    }

    public OffsetDateTimeExpScalar2(String opName, Exp<L> left, R right, BiFunction<Series<L>, R, Series<OffsetDateTime>> op) {
        super(opName, OffsetDateTime.class, left, right, op);
    }
}
