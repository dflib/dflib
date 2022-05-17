package com.nhl.dflib.exp.num;

import com.nhl.dflib.Exp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class FloatExp1<F> extends MapExp1<F, Float> implements NumExp<Float> {

    public static <F> FloatExp1<F> mapVal(String opName, Exp<F> exp, Function<F, Float> op) {
        return new FloatExp1<>(opName, exp, valToSeries(op));
    }

    public FloatExp1(String opName, Exp<F> exp, Function<Series<F>, Series<Float>> op) {
        super(opName, Float.class, exp, op);
    }
}
