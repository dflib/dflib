package com.nhl.dflib.exp.num;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class LongExp1<F> extends MapExp1<F, Long> implements NumExp<Long> {

    public static <F> LongExp1<F> mapVal(String opName, Exp<F> exp, Function<F, Long> op) {
        return new LongExp1<>(opName, exp, valToSeries(op));
    }

    public LongExp1(String opName, Exp<F> exp, Function<Series<F>, Series<Long>> op) {
        super(opName, Long.class, exp, op);
    }
}
