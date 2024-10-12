package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.exp.map.MapExp1;

import java.util.function.Function;


public class StrExp1<F> extends MapExp1<F, String> implements StrExp {

    public static <F> StrExp1<F> mapVal(String opName, Exp<F> exp, Function<F, String> op) {
        return new StrExp1<>(opName, exp, valToSeries(op));
    }

    public StrExp1(String opName, Exp<F> exp, Function<Series<F>, Series<String>> op) {
        super(opName, String.class, exp, op);
    }
}
