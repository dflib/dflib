package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.seriesexp.ColumnExp;
import com.nhl.dflib.NumericSeriesExp;

import java.math.BigDecimal;

/**
 * @since 0.11
 */
public class DecimalColumn extends ColumnExp<BigDecimal> implements NumericSeriesExp<BigDecimal> {

    public DecimalColumn(String name) {
        super(name, BigDecimal.class);
    }

    public DecimalColumn(int position) {
        super(position, BigDecimal.class);
    }
}
