package org.dflib.exp.num;

import org.dflib.BigIntegerExp;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.math.BigInteger;
import java.util.function.BiFunction;

public class BigIntegerExp2<R> extends MapExp2<BigInteger, R, BigInteger> implements BigIntegerExp {

    public static <R> BigIntegerExp2 mapVal(String opName, Exp<BigInteger> left, Exp<R> right, BiFunction<BigInteger, R, BigInteger> op) {
        return new BigIntegerExp2(opName, left, right, valToSeries(op));
    }

    protected BigIntegerExp2(
            String opName,
            Exp<BigInteger> left,
            Exp<R> right,
            BiFunction<Series<BigInteger>, Series<R>, Series<BigInteger>> op) {
        super(opName, BigInteger.class, left, right, op);
    }
}
