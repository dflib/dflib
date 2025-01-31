package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.math.BigInteger;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class BigintExp1<F> extends MapExp1<F, BigInteger> implements NumExp<BigInteger> {


    public static <F> BigintExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<BigInteger>> op) {
        return new BigintExp1<>(opName, exp, op);
    }

    public static <F> BigintExp1<F> mapVal(String opName, Exp<F> exp, Function<F, BigInteger> op) {
        return new BigintExp1<>(opName, exp, valToSeries(op));
    }

    public BigintExp1(String opName, Exp<F> exp, Function<Series<F>, Series<BigInteger>> op) {
        super(opName, BigInteger.class, exp, op);
    }

    @Override
    public NumExp<BigInteger> castAsBigint() {
        return this;
    }
}
