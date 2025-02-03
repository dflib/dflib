package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;

import java.math.BigInteger;

/**
 * @since 2.0.0
 */
public class BigintScalarExp extends ScalarExp<BigInteger> implements NumExp<BigInteger> {

    public BigintScalarExp(BigInteger value) {
        super(value, BigInteger.class);
    }

    @Override
    public NumExp<BigInteger> castAsBigint() {
        return this;
    }
}
