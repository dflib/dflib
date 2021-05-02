package com.nhl.dflib.seriesexp.num;

import com.nhl.dflib.seriesexp.ColumnExp;
import com.nhl.dflib.NumericSeriesExp;

/**
 * @since 0.11
 */
public class DoubleColumn extends ColumnExp<Double> implements NumericSeriesExp<Double> {

    public DoubleColumn(String name) {
        super(name, Double.class);
    }

    public DoubleColumn(int position) {
        super(position, Double.class);
    }
}
