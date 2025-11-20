package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.Column;

import java.math.BigInteger;

/**
 * @since 2.0.0
 */
public class BigintColumn extends Column<BigInteger> implements NumExp<BigInteger> {

    public BigintColumn(String name) {
        super(name, BigInteger.class);
    }

    public BigintColumn(int position) {
        super(position, BigInteger.class);
    }

    @Override
    public String getColumnName() {
        return position >= 0 ? "bigint(" + position + ")" : name;
    }

    @Override
    public NumExp<BigInteger> castAsBigint() {
        return this;
    }
}
