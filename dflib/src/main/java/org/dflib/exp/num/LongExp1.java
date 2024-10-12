package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.util.function.Function;


public class LongExp1<F> extends MapExp1<F, Long> implements NumExp<Long> {


    public static <F> LongExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<Long>> op) {
        return new LongExp1<>(opName, exp, op);
    }

    public static <F> LongExp1<F> mapVal(String opName, Exp<F> exp, Function<F, Long> op) {
        return new LongExp1<>(opName, exp, valToSeries(op));
    }

    public LongExp1(String opName, Exp<F> exp, Function<Series<F>, Series<Long>> op) {
        super(opName, Long.class, exp, op);
    }

    @Override
    public NumExp<Long> castAsLong() {
        return this;
    }
}
