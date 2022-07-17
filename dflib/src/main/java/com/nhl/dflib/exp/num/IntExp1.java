package com.nhl.dflib.exp.num;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class IntExp1<F> extends MapExp1<F, Integer> implements NumExp<Integer> {

    /**
     * @since 0.14
     */
    public static <F> IntExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<Integer>> op) {
        return new IntExp1<>(opName, exp, op);
    }

    public static <F> IntExp1<F> mapVal(String opName, Exp<F> exp, Function<F, Integer> op) {
        return new IntExp1<>(opName, exp, valToSeries(op));
    }

    public IntExp1(String opName, Exp<F> exp, Function<Series<F>, Series<Integer>> op) {
        super(opName, Integer.class, exp, op);
    }
}
