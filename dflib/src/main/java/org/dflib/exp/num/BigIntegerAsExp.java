package org.dflib.exp.num;

import org.dflib.BigIntegerExp;
import org.dflib.Exp;
import org.dflib.exp.AsExp;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @since 2.0.0
 */
public class BigIntegerAsExp extends AsExp<BigInteger> implements BigIntegerExp {

    public BigIntegerAsExp(String name, Exp<BigInteger> delegate) {
        super(name, delegate);
    }

    @Override
    public BigIntegerExp as(String name) {
        return Objects.equals(name, this.name) ? this : BigIntegerExp.super.as(name);
    }
}
