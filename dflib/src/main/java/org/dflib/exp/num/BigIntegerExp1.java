package org.dflib.exp.num;

import org.dflib.BigIntegerExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.math.BigInteger;
import java.util.function.Function;


public class BigIntegerExp1<F> extends MapExp1<F, BigInteger> implements BigIntegerExp {


    public static <F> BigIntegerExp1<F> map(String opName, Exp<F> exp, Function<Series<F>, Series<BigInteger>> op) {
        return new BigIntegerExp1<>(opName, exp, op);
    }

    public static <F> BigIntegerExp1<F> mapVal(String opName, Exp<F> exp, Function<F, BigInteger> op) {
        return new BigIntegerExp1<>(opName, exp, valToSeries(op));
    }

    public BigIntegerExp1(String opName, Exp<F> exp, Function<Series<F>, Series<BigInteger>> op) {
        super(opName, BigInteger.class, exp, op);
    }
}
