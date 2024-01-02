package org.dflib.exp.num;

import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @since 0.11
 */
public class DecimalExp1<F> extends MapExp1<F, BigDecimal> implements DecimalExp {

    /**
     * @since 0.14
     */
    public static <F> DecimalExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<BigDecimal>> op) {
        return new DecimalExp1<>(opName, exp, op);
    }

    public static <F> DecimalExp1<F> mapVal(String opName, Exp<F> exp, Function<F, BigDecimal> op) {
        return new DecimalExp1<>(opName, exp, valToSeries(op));
    }

    public DecimalExp1(String opName, Exp<F> exp, Function<Series<F>, Series<BigDecimal>> op) {
        super(opName, BigDecimal.class, exp, op);
    }
}
