package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.math.BigInteger;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class BigintReduceExp1<F> extends ReduceExp1<F, BigInteger> implements NumExp<BigInteger> {

    public BigintReduceExp1(String opName, Exp<F> exp, Function<Series<F>, BigInteger> op, Condition filter) {
        super(opName, BigInteger.class, exp, op, filter);
    }

    @Override
    public Series<BigInteger> eval(Series<?> s) {
        return new SingleValueSeries<>(reduce(s), s.size());
    }

    @Override
    public Series<BigInteger> eval(DataFrame df) {
        return new SingleValueSeries<>(reduce(df), df.height());
    }

    @Override
    public NumExp<BigInteger> castAsBigint() {
        return this;
    }
}
