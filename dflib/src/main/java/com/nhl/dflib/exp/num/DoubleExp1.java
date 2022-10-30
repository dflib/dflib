package com.nhl.dflib.exp.num;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class DoubleExp1<F> extends MapExp1<F, Double> implements NumExp<Double> {

    /**
     * @since 0.14
     */
    public static <F> DoubleExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<Double>> op) {
        return new DoubleExp1<>(opName, exp, op);
    }

    public static <F> DoubleExp1<F> mapVal(String opName, Exp<F> exp, Function<F, Double> op) {
        return new DoubleExp1<>(opName, exp, valToSeries(op));
    }

    public DoubleExp1(String opName, Exp<F> exp, Function<Series<F>, Series<Double>> op) {
        super(opName, Double.class, exp, op);
    }

    @Override
    public NumExp<Double> castAsDouble() {
        return this;
    }
}
