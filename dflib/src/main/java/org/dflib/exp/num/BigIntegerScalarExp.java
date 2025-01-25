package org.dflib.exp.num;

import org.dflib.BigIntegerExp;
import org.dflib.exp.ScalarExp;

import java.math.BigInteger;

/**
 * @since 2.0.0
 */
public class BigIntegerScalarExp extends ScalarExp<BigInteger> implements BigIntegerExp {

    public BigIntegerScalarExp(BigInteger value) {
        super(value, BigInteger.class);
    }
}
