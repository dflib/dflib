package com.nhl.dflib.exp.num;

import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExp1;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DecimalExp1<F> extends MapExp1<F, BigDecimal> implements DecimalExp {

    public static <F> DecimalExp1<F> mapVal(String opName, Exp<F> exp, Function<F, BigDecimal> op) {
        return new DecimalExp1<>(opName, exp, valToSeries(op));
    }

    public DecimalExp1(String opName, Exp<F> exp, Function<Series<F>, Series<BigDecimal>> op) {
        super(opName, BigDecimal.class, exp, op);
    }
}
