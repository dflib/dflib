package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.util.function.Function;

/**
 * @since 1.1.0
 */
public class FloatExp1<F> extends MapExp1<F, Float> implements NumExp<Float> {

    public static <F> FloatExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<Float>> op) {
        return new FloatExp1<>(opName, exp, op);
    }

    public static <F> FloatExp1<F> mapVal(String opName, Exp<F> exp, Function<F, Float> op) {
        return new FloatExp1<>(opName, exp, valToSeries(op));
    }

    public FloatExp1(String opName, Exp<F> exp, Function<Series<F>, Series<Float>> op) {
        super(opName, Float.class, exp, op);
    }

    @Override
    public NumExp<Float> castAsFloat() {
        return this;
    }
}
