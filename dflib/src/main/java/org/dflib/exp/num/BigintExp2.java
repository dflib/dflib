package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp2;

import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class BigintExp2<R> extends MapExp2<BigInteger, R, BigInteger> implements NumExp<BigInteger> {

    public static <R> BigintExp2 mapVal(String opName, Exp<BigInteger> left, Exp<R> right, BiFunction<BigInteger, R, BigInteger> op) {
        return new BigintExp2(opName, left, right, valToSeries(op));
    }

    protected BigintExp2(
            String opName,
            Exp<BigInteger> left,
            Exp<R> right,
            BiFunction<Series<BigInteger>, Series<R>, Series<BigInteger>> op) {
        super(opName, BigInteger.class, left, right, op);
    }

    @Override
    public NumExp<BigInteger> castAsBigint() {
        return this;
    }
}
