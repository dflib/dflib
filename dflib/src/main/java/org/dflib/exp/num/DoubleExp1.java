package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.util.function.Function;


public class DoubleExp1<F> extends MapExp1<F, Double> implements NumExp<Double> {


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
