package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.time.OffsetDateTime;
import java.util.function.Function;

/**
 * @since 1.1.0
 */
public class OffsetDateTimeExp1<F> extends MapExp1<F, OffsetDateTime> implements OffsetDateTimeExp {

    public static <F> OffsetDateTimeExp1<F> mapVal(String opName, Exp<F> exp, Function<F, OffsetDateTime> op) {
        return new OffsetDateTimeExp1<>(opName, exp, valToSeries(op));
    }

    public OffsetDateTimeExp1(String opName, Exp<F> exp, Function<Series<F>, Series<OffsetDateTime>> op) {
        super(opName, OffsetDateTime.class, exp, op);
    }
}
