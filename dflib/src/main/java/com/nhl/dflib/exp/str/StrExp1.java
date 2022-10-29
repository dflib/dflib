package com.nhl.dflib.exp.str;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.map.MapExp1;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class StrExp1<F> extends MapExp1<F, String> implements StrExp {

    public static <F> StrExp1<F> mapVal(String opName, Exp<F> exp, Function<F, String> op) {
        return new StrExp1<>(opName, exp, valToSeries(op));
    }

    public StrExp1(String opName, Exp<F> exp, Function<Series<F>, Series<String>> op) {
        super(opName, String.class, exp, op);
    }
}
