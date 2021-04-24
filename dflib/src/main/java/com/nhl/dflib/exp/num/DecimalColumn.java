package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.exp.NumericExp;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class DecimalColumn extends ColumnExp<BigDecimal> implements NumericExp<BigDecimal> {

    public DecimalColumn(String name) {
        super(name, BigDecimal.class);
    }

    public DecimalColumn(int position) {
        super(position, BigDecimal.class);
    }
}
