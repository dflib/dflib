package org.dflib.exp.num;

import org.dflib.BigIntegerExp;
import org.dflib.exp.Column;

import java.math.BigInteger;


public class BigIntegerColumn extends Column<BigInteger> implements BigIntegerExp {

    public BigIntegerColumn(String name) {
        super(name, BigInteger.class);
    }

    public BigIntegerColumn(int position) {
        super(position, BigInteger.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$bigInteger(" + position + ")" : name;
    }
}
